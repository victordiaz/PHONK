/*
 * Part of Phonk http://www.phonk.io
 * A prototyping platform for Android devices
 *
 * Copyright (C) 2013 - 2017 Victor Diaz Barrales @victordiaz (Protocoder)
 * Copyright (C) 2017 - Victor Diaz Barrales @victordiaz (Phonk)
 *
 * Phonk is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Phonk is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Phonk. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package io.phonk.runner.apprunner.api.boards;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import io.phonk.runner.apidoc.annotation.PhonkMethod;
import io.phonk.runner.apidoc.annotation.PhonkMethodParam;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.api.ProtoBase;
import io.phonk.runner.apprunner.api.common.ReturnInterface;
import io.phonk.runner.apprunner.api.common.ReturnObject;
import io.phonk.runner.base.utils.MLog;

public class PSerial extends ProtoBase {
    private final String TAG = "qww"; // PSerial.class.getSimpleName();

    private static final String ACTION_USB_PERMISSION = "ACTION_USB_PERMISSION";

    private ReturnInterface mCallbackData;
    private ReturnInterface mCallbackSerialStatus;

    private UsbDevice mDevice;
    private UsbManager mUsbManager;
    private UsbDeviceConnection mConnection;
    private UsbSerialDevice mSerialPort;

    private boolean mSerialPortConnected = false;
    private int mBaudsRate = 9600;
    private boolean isReturningFullLine = true;

    public PSerial(AppRunner appRunner) {
        super(appRunner);
    }

    @PhonkMethod(description = "starts serial", example = "")
    public void start() {
        getAppRunner().whatIsRunning.add(this);
        mUsbManager = (UsbManager) getContext().getSystemService(Context.USB_SERVICE);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        // filter.addAction(ACTION_USB_DETACHED);
        // filter.addAction(ACTION_USB_ATTACHED);
        getContext().registerReceiver(usbReceiver, filter);
        findSerialPortDevice();
    }

    @PhonkMethod(description = "stop serial", example = "")
    @PhonkMethodParam(params = {})
    public void stop() {
        if (mSerialPortConnected) {
            mSerialPort.close();
        }
        getContext().unregisterReceiver(usbReceiver);
    }

    @PhonkMethod(description = "sends commands to the serial")
    @PhonkMethodParam(params = {"data"})
    public void write(String data) {
        if (!mSerialPortConnected) return;
        mSerialPort.write(data.getBytes());
    }

    public void onSerialStatus(ReturnInterface cb) {
        mCallbackSerialStatus = cb;
    }

    public PSerial onNewData(ReturnInterface cb) {
        mCallbackData = cb;
        return this;
    }


    private void findSerialPortDevice() {
        // This snippet will try to open the first encountered usb mDevice connected, excluding usb root hubs
        HashMap<String, UsbDevice> usbDevices = mUsbManager.getDeviceList();
        if (!usbDevices.isEmpty()) {

            // first, dump the hashmap for diagnostic purposes
            for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                mDevice = entry.getValue();
                MLog.d(TAG, String.format("USBDevice.HashMap (vid:pid) (%X:%X)-%b class:%X:%X name:%s",
                        mDevice.getVendorId(), mDevice.getProductId(),
                        UsbSerialDevice.isSupported(mDevice),
                        mDevice.getDeviceClass(), mDevice.getDeviceSubclass(),
                        mDevice.getDeviceName()));
            }

            for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                mDevice = entry.getValue();
                int deviceVID = mDevice.getVendorId();
                int devicePID = mDevice.getProductId();

//                if (deviceVID != 0x1d6b && (devicePID != 0x0001 && devicePID != 0x0002 && devicePID != 0x0003) && deviceVID != 0x5c6 && devicePID != 0x904c) {
                if (UsbSerialDevice.isSupported(mDevice)) {
                    // There is a supported mDevice connected - request permission to access it.
                    MLog.d(TAG, "requesting");
                    requestUserPermission();
                    break;
                } else {
                    mConnection = null;
                    mDevice = null;
                }
            }
            if (mDevice == null) {
                // There are no USB devices connected (but usb host were listed). Send an intent to MainActivity.
                error("No USB connected");
            }
        } else {
            MLog.d(TAG, "findSerialPortDevice() usbManager returned empty mDevice list." );
            // There is no USB devices connected. Send an intent to MainActivity
            error("No USB connected");
        }
    }

    private void error(String msg) {
        MLog.d(TAG, "error " + msg);
    }


    /*
     * Request user permission. The response will be received in the BroadcastReceiver
     */
    private void requestUserPermission() {
        MLog.d(TAG, String.format("requestUserPermission(%X:%X)", mDevice.getVendorId(), mDevice.getProductId() ) );
        PendingIntent mPendingIntent = PendingIntent.getBroadcast(getContext(), 0, new Intent(ACTION_USB_PERMISSION), 0);
        mUsbManager.requestPermission(mDevice, mPendingIntent);
    }

    /*
     * Different notifications from OS will be received here (USB attached, detached, permission responses...)
     * About BroadcastReceiver: http://developer.android.com/reference/android/content/BroadcastReceiver.html
     */
    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MLog.d(TAG, "onReceive " + intent.getAction().toString());
            if (intent.getAction().equals(ACTION_USB_PERMISSION)) {
                final boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);

                mHandler.post(() -> {
                    ReturnObject o = new ReturnObject();
                    o.put("usbPermission", granted);
                    if (mCallbackData != null) mCallbackData.event(o);
                });

                // User accepted our USB connection. Try to open the mDevice as a serial port
                if (granted) {
                    MLog.d(TAG, "onReceive granted " + granted);
                    // Intent intent = new Intent(ACTION_USB_PERMISSION_GRANTED);
                    // context.sendBroadcast(intent);
                    mConnection = mUsbManager.openDevice(mDevice);
                    mHandler.post(() -> {
                        ReturnObject o = new ReturnObject();
                        o.put("usbPermission", granted);
                        if (mCallbackSerialStatus != null) mCallbackSerialStatus.event(o);
                    });
                    new ConnectionThread().start();
                // User not accepted our USB connection. Send an Intent to the Main Activity
                } else {
                    // Intent intent = new Intent(ACTION_USB_PERMISSION_NOT_GRANTED);
                    // context.sendBroadcast(intent);
                }
            }
            /*
            else if (intent.getAction().equals(ACTION_USB_ATTACHED)) {
                if (!mSerialPortConnected) {
                    findSerialPortDevice(); // A USB mDevice has been attached. Try to open it as a Serial port
                }
            } else if (intent.getAction().equals(ACTION_USB_DETACHED)) {
                // Usb mDevice was disconnected. send an intent to the Main Activity
                Intent intent = new Intent(ACTION_USB_DISCONNECTED);
                context.sendBroadcast(intent);
                if (mSerialPortConnected) {
                    mSerialPort.close();
                }
                mSerialPortConnected = false;
            }
            */
        }
    };

    /*
     * A simple thread to open a serial port.
     * Although it should be a fast operation. moving usb operations away from UI thread is a good thing.
     */
    private class ConnectionThread extends Thread {
        @Override
        public void run() {
            mSerialPort = UsbSerialDevice.createUsbSerialDevice(mDevice, mConnection);
            if (mSerialPort != null) {
                if (mSerialPort.open()) {
                    mSerialPortConnected = true;
                    mSerialPort.setBaudRate(mBaudsRate);
                    mSerialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                    mSerialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                    mSerialPort.setParity(UsbSerialInterface.PARITY_NONE);
                    /**
                     * Current flow control Options:
                     * UsbSerialInterface.FLOW_CONTROL_OFF
                     * UsbSerialInterface.FLOW_CONTROL_RTS_CTS only for CP2102 and FT232
                     * UsbSerialInterface.FLOW_CONTROL_DSR_DTR only for CP2102 and FT232
                     */
                    mSerialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                    mSerialPort.read(mUSBReadCallback);
                    mSerialPort.getCTS(mCTSCallback);
                    mSerialPort.getDSR(mDSRCallback);

                    //
                    // Some Arduinos would need some sleep because firmware wait some time to know whether a new sketch is going
                    // to be uploaded or not
                    try {
                        Thread.sleep(1000); // sleep some. YMMV with different chips.
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    MLog.d(TAG, "serial connected");
                } else {
                    // no connection
                    mSerialPortConnected = false;
                }
                mHandler.post(() -> {
                    ReturnObject o = new ReturnObject();
                    o.put("connected", mSerialPortConnected);
                    if (mCallbackSerialStatus != null) mCallbackSerialStatus.event(o);
                });

            } else {
                // No driver for given device, even generic CDC driver could not be loaded
                // Intent intent = new Intent(ACTION_USB_NOT_SUPPORTED);
                // getContext().sendBroadcast(intent);
            }
        }
    }

    /*
     *  Data received from serial port will be received here. Just populate onReceivedData with your code
     *  In this particular example. byte stream is converted to String and send to UI thread to
     *  be treated there.
     */
    private UsbSerialInterface.UsbReadCallback mUSBReadCallback = new UsbSerialInterface.UsbReadCallback() {
        private String returnLine;

        @Override
        public void onReceivedData(byte[] arg0) {
            try {
                String data = new String(arg0, "UTF-8");
                MLog.d(TAG, "--> " + data);

                if (isReturningFullLine) {
                    returnLine = returnLine + data;
                    int newLineIndex = returnLine.indexOf('\n');
                    MLog.d(TAG, "index " + newLineIndex);
                    String msgReturn = "";
                    if (newLineIndex != -1) {
                        msgReturn = returnLine.substring(0, newLineIndex);
                        returnLine = returnLine.substring(newLineIndex + 1);
                    }
                    // MLog.d(TAG, msg);
                    if (msgReturn.trim().equals("") == false) {
                        final String finalMsgReturn = msgReturn;
                        mHandler.post(() -> {
                            ReturnObject o = new ReturnObject();
                            o.put("data", finalMsgReturn);
                            if (mCallbackData != null) mCallbackData.event(o);
                        });
                    }
                } else {
                    // returnData = returnLine;
                }


            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    };

    /*
     * State changes in the CTS line will be received here
     */
    private UsbSerialInterface.UsbCTSCallback mCTSCallback = state -> {
        if(mHandler != null) {
            // mHandler.obtainMessage(CTS_CHANGE).sendToTarget();
        }
    };

    /*
     * State changes in the DSR line will be received here
     */
    private UsbSerialInterface.UsbDSRCallback mDSRCallback = state -> {
        if(mHandler != null) {
            // mHandler.obtainMessage(DSR_CHANGE).sendToTarget();
        }
    };


    @Override
    public void __stop() {
        stop();
    }
}