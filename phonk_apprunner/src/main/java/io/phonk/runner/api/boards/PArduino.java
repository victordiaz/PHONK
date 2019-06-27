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

package io.phonk.runner.api.boards;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import com.physicaloid.lib.Boards;
import com.physicaloid.lib.Physicaloid;
import com.physicaloid.lib.Physicaloid.UploadCallBack;
import com.physicaloid.lib.programmer.avr.UploadErrors;
import com.physicaloid.lib.usb.driver.uart.ReadLisener;
import com.physicaloid.lib.usb.driver.uart.UartConfig;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;

import io.phonk.runner.api.ProtoBase;
import io.phonk.runner.apidoc.annotation.ProtoMethod;
import io.phonk.runner.apidoc.annotation.ProtoMethodParam;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.base.utils.MLog;

public class PArduino extends ProtoBase {

    private final String TAG = "PArduino";
    private Physicaloid mPhysicaloid;
    private boolean started = false;
    private String msg = "";
    private String mEndLine = "";


    public static final Boards ARDUINO_UNO = Boards.ARDUINO_UNO;
    public static final Boards ARDUINO_DUEMILANOVE_328 = Boards.ARDUINO_DUEMILANOVE_328;
    public static final Boards ARDUINO_DUEMILANOVE_168 = Boards.ARDUINO_DUEMILANOVE_168;
    public static final Boards ARDUINO_NANO_328 = Boards.ARDUINO_NANO_328;
    public static final Boards ARDUINO_NANO_168 = Boards.ARDUINO_NANO_168;
    public static final Boards ARDUINO_MEGA_2560_ADK = Boards.ARDUINO_MEGA_2560_ADK;
    public static final Boards ARDUINO_MEGA_1280 = Boards.ARDUINO_MEGA_1280;
    public static final Boards ARDUINO_MINI_328 = Boards.ARDUINO_MINI_328;
    public static final Boards ARDUINO_MINI_168 = Boards.ARDUINO_MINI_168;
    public static final Boards ARDUINO_ETHERNET = Boards.ARDUINO_ETHERNET;
    public static final Boards ARDUINO_FIO = Boards.ARDUINO_FIO;
    public static final Boards ARDUINO_BT_328 = Boards.ARDUINO_BT_328;
    public static final Boards ARDUINO_BT_168 = Boards.ARDUINO_BT_168;
    public static final Boards ARDUINO_LILYPAD_328 = Boards.ARDUINO_LILYPAD_328;
    public static final Boards ARDUINO_LILYPAD_168 = Boards.ARDUINO_LILYPAD_168;

    public static final Boards ARDUINO_PRO_5V_328 = Boards.ARDUINO_PRO_5V_328;
    public static final Boards ARDUINO_PRO_5V_168 = Boards.ARDUINO_PRO_5V_168;
    public static final Boards ARDUINO_PRO_33V_328 = Boards.ARDUINO_PRO_33V_328;
    public static final Boards ARDUINO_PRO_33V_168 = Boards.ARDUINO_PRO_33V_168;
    public static final Boards ARDUINO_NG_168 = Boards.ARDUINO_NG_168;
    public static final Boards ARDUINO_NG_8 = Boards.ARDUINO_NG_8;
    public static final Boards BALANDUINO = Boards.BALANDUINO;
    public static final Boards POCKETDUINO = Boards.POCKETDUINO;
    public static final Boards PERIDOT = Boards.PERIDOT;

    public static final Boards FREADUINO = Boards.ARDUINO_UNO;
    public static final Boards BQ_ZUM = Boards.ARDUINO_BT_328;

    public static final Boards NONE = Boards.NONE;


    BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                MLog.d(TAG, "Device attached");

            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                MLog.d(TAG, "Device detached");

                //  } else if (ACTION_USB_PERMISSION.equals(action)) {
                //      MLog.d(TAG, "Request permission");
                //
            }
        }
    };

    public PArduino(AppRunner appRunner) {
        super(appRunner);
    }

    public void start() {
        getAppRunner().whatIsRunning.add(this);
        mPhysicaloid = new Physicaloid(getContext());
        open();
    }

    // Initializes arduino board
    public void start(int bauds, String endline, onReadCB callbackfn) {
        getAppRunner().whatIsRunning.add(this);

        mEndLine = endline;

        if (!started) {
            started = true;
            MLog.d("PArduino", "start ");

            UsbManager manager = (UsbManager) getContext().getSystemService(Context.USB_SERVICE);
            HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
            Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
            while (deviceIterator.hasNext()) {
                UsbDevice device = deviceIterator.next();
                MLog.network(getContext(), TAG, "USB " + device.getDeviceName());
                //your code
            }

            mPhysicaloid = new Physicaloid(getContext());
            open();
            mPhysicaloid.setBaudrate(bauds);

//            int mFlowControl = UartConfig.FLOW_CONTROL_OFF;
//            int mDataBits = UartConfig.DATA_BITS8;
//            int mStopBits = UartConfig.STOP_BITS1;
//            int mParity = UartConfig.PARITY_NONE;
//
//            boolean dtrOn=false;
//            boolean rtsOn=false;
//            if(mFlowControl == UartConfig.FLOW_CONTROL_ON) {
//                dtrOn = true;
//                rtsOn = true;
//            }
            //    mPhysicaloid.setConfig(new UartConfig(bauds, mDataBits, mStopBits, mParity, dtrOn, rtsOn));


            onRead(callbackfn);
        }
    }

    // Opens mContext device and communicate USB UART by default settings
    public void open() {
        if (mPhysicaloid.isOpened()) {
            MLog.d(TAG, "The device is opened");
            return;
        }

        if (mPhysicaloid.open()) {
            MLog.d(TAG, "Device opened");
        } else {
            MLog.d(TAG, "Cannot open the device");
        }

    }

    // Closes mContext device
    public void close() {
        if (mPhysicaloid.close()) {
            //  clear read callback
            mPhysicaloid.clearReadListener();
            MLog.d(TAG, "Device closed");
        } else {
            MLog.d(TAG, "Cannot close the device");
        }
    }


    @ProtoMethod(description = "sends commands to arduino board", example = "arduino.write(\"LEDON\");")
    public void write(String cmd) {
        if (mPhysicaloid.isOpened()) {
            byte[] buf = cmd.getBytes();
            mPhysicaloid.write(buf, buf.length);
            MLog.d(TAG, "Command sent to the device");
        } else {
            MLog.d(TAG, "Cannot write to the device. The device is not opened");
        }
    }

    // --------- onReadCB ---------//
    public interface onReadCB {
        void event(String responseString);
    }

    @ProtoMethod(description = "reads from the arduino board", example = "arduino.read()")
    public String read() {
        String str = "";
        if (mPhysicaloid.isOpened()) {
            byte[] buf = new byte[256];
            int readSize = mPhysicaloid.read(buf);

            if (readSize > 0) {
                str = new String(buf, StandardCharsets.UTF_8);
            }
        } else {
            MLog.d(TAG, "Cannot read from the device. The device is not opened");
        }
        return str;
    }


    @ProtoMethod(description = "adds a read callback that is called when one or more bytes are read", example = "")
    @ProtoMethodParam(params = {"function(data)"})
    public void onRead(final onReadCB callbackfn) {
        if (mPhysicaloid.isOpened()) {
            mPhysicaloid.addReadListener(new ReadLisener() {
                String readStr;

                // callback when reading one or more size buffer
                @Override
                public void onRead(int size) {
                    byte[] buf = new byte[size];
                    mPhysicaloid.read(buf, size);
                    readStr = new String(buf, StandardCharsets.UTF_8);
                    // MLog.d(TAG, " " + readStr);


                    //MLog.network(mContext, TAG, "msg " + msg);
                    //MLog.network(mContext, TAG, "readStr " + readStr);

                    if (!mEndLine.isEmpty()) {
                        msg = msg + readStr;

                        int newLineIndex = msg.indexOf('\n');
                        //MLog.network(mContext, TAG, "index " + newLineIndex);
                        String msgReturn = "";

                        if (newLineIndex != -1) {
                            msgReturn = msg.substring(0, newLineIndex);
                            msg = msg.substring(newLineIndex + 1);
                            //MLog.network(mContext, TAG, "msgReturn " + msgReturn);

                        }
                        if (msgReturn.trim().equals("") == false) {
                            final String finalMsgReturn = msgReturn;
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    callbackfn.event(finalMsgReturn);
                                }
                            });
                        }
                    } else {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callbackfn.event(readStr);
                            }
                        });
                    }
                }
            });
        }
    }

    // --------- uploadCallback ---------//
    public interface uploadCB {
        void event(int progress);
    }

    // Uploads mContext binary file to mContext device on background process. No need to open().
    // * @param board board profile e.g. Packages.com.physicaloid.lib.Boards.ARDUINO_UNO
    // * @param fileName mContext binary file name e.g. Blink.hex
    // * @param callbackfn callback when the upload has been completed (success, fail or error)
    //

    @ProtoMethod(description = "uploads a binary file to a device on background process", example = "")
    @ProtoMethodParam(params = {"board", "fileName", "function(error)"})
    public void upload(Boards board, String fileName, final uploadCB callbackfn) {
        if (mPhysicaloid.isOpened()) {
            // Build the absolute path
            String filePath = getAppRunner().getProject().getFullPathForFile(fileName);

            callbackfn.event(0);
            // Check if the fileName includes the .hex extension
            if (!filePath.toLowerCase().endsWith(".hex")) {
                MLog.d(TAG, "Cannot upload the sketch. The file must have a .hex extension");

                return;
            }

            try {
                mPhysicaloid.upload(board, filePath, new UploadCallBack() {
                    String responseStr;

                    @Override
                    public void onPreUpload() {
                        MLog.d(TAG, "Upload : Start");
                    }

                    @Override
                    public void onUploading(int value) {
                        uploadCallbackEvent(value, callbackfn);
                    }

                    @Override
                    public void onPostUpload(boolean success) {
                        //MLog.network(mContext, TAG, "5");

                        if (success) {
                            //     uploadCallbackEvent(100, callbackfn);
                        } else {

                            uploadCallbackEvent(-1, callbackfn);
                        }
                    }

                    @Override
                    public void onCancel() {
                        uploadCallbackEvent(-1, callbackfn);
                    }

                    @Override
                    public void onError(UploadErrors err) {
                        MLog.d(TAG, "Error  : " + err.toString());

                        uploadCallbackEvent(-1, callbackfn);
                    }
                });
            } catch (RuntimeException e) {
                MLog.d(TAG, e.toString());
            }

        } else {
            MLog.d(TAG, "Cannot upload the sketch. The device is not opened");
        }
    }

    private void uploadCallbackEvent(final int value, final uploadCB callbackfn) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callbackfn.event(value);
            }
        });
    }


    @ProtoMethod(description = "uploads a binary file to a device on background process", example = "")
    @ProtoMethodParam(params = {"board", "fileName"})
    public void upload(Boards board, String fileName) {

        upload(board, fileName, new uploadCB() {
            @Override
            public void event(int progress) {

            }
        });
    }


    @ProtoMethod(description = "sets baud rate", example = "arduino.setBaudrate(9600)")
    @ProtoMethodParam(params = {"baudrate"})
    public boolean setBaudrate(int baudrate) {
        try {
            return mPhysicaloid.setBaudrate(baudrate);
        } catch (RuntimeException e) {
            return false;
        }
    }


    @ProtoMethod(description = "returns a list of the supported devices that you can use with the \"upload\" function", example = "arduino.getSupportedDevices()")
    public Boards[] getSupportedDevices() {
        MLog.d(TAG, Boards.ARDUINO_BT_328.toString());

        return com.physicaloid.lib.Boards.values();
        /*
        Boards[] boards = new Boards[]{
                Boards.ARDUINO_UNO,
                Boards.ARDUINO_DUEMILANOVE_328,
                Boards.ARDUINO_DUEMILANOVE_168,
                Boards.ARDUINO_NANO_328,
                Boards.ARDUINO_NANO_168,
                Boards.ARDUINO_MEGA_2560_ADK,
                Boards.ARDUINO_MEGA_1280,
                Boards.ARDUINO_MINI_328,
                Boards.ARDUINO_MINI_168,
                Boards.ARDUINO_ETHERNET,
                Boards.ARDUINO_FIO,
                Boards.ARDUINO_BT_328,
                Boards.ARDUINO_BT_168,
                Boards.ARDUINO_LILYPAD_328,
                Boards.ARDUINO_LILYPAD_168,
                Boards.ARDUINO_PRO_5V_328,
                Boards.ARDUINO_PRO_5V_168,
                Boards.ARDUINO_PRO_33V_328,
                Boards.ARDUINO_PRO_33V_168,
                Boards.ARDUINO_NG_168,
                Boards.ARDUINO_NG_8,
                Boards.BALANDUINO,
                Boards.POCKETDUINO,
                Boards.PERIDOT,
                Boards.NONE};

        return boards;
        */
    }


    @ProtoMethod(description = "sets serial configuration", example = "")
    @ProtoMethodParam(params = {"settings"})
    public void setConfig(UartConfig settings) {
        try {
            mPhysicaloid.setConfig(settings);
        } catch (RuntimeException e) {
            MLog.d(TAG, e.toString());
        }
    }


    @ProtoMethod(description = "sets data bits", example = "")
    @ProtoMethodParam(params = {"dataBits"})
    public boolean setDataBits(int dataBits) {
        try {
            return mPhysicaloid.setDataBits(dataBits);
        } catch (RuntimeException e) {
            MLog.d(TAG, e.toString());
            return false;
        }
    }


    @ProtoMethod(description = "sets parity bits", example = "")
    @ProtoMethodParam(params = {"parity"})
    public boolean setParity(int parity) {
        try {
            return mPhysicaloid.setParity(parity);
        } catch (RuntimeException e) {
            MLog.d(TAG, e.toString());
            return false;
        }
    }


    @ProtoMethod(description = "sets stop bits", example = "")
    @ProtoMethodParam(params = {"stopBits"})
    public boolean setStopBits(int stopBits) {
        try {
            return mPhysicaloid.setStopBits(stopBits);
        } catch (RuntimeException e) {
            MLog.d(TAG, e.toString());
            return false;
        }
    }


    @ProtoMethod(description = "sets flow control DTR/RTS", example = "")
    @ProtoMethodParam(params = {"stopBits"})
    public boolean setDtrRts(boolean dtrOn, boolean rtsOn) {
        try {
            return mPhysicaloid.setDtrRts(dtrOn, rtsOn);
        } catch (RuntimeException e) {
            MLog.d(TAG, e.toString());
            return false;
        }
    }

    @Override
    public void __stop() {
        close();
    }
}