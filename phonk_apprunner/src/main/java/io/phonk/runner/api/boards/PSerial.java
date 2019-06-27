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

import android.content.Context;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.phonk.runner.api.ProtoBase;
import io.phonk.runner.api.common.ReturnInterface;
import io.phonk.runner.api.common.ReturnObject;
import io.phonk.runner.apidoc.annotation.ProtoMethod;
import io.phonk.runner.apidoc.annotation.ProtoMethodParam;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.base.utils.MLog;

public class PSerial extends ProtoBase {

    private final int mBauds;
    private String receivedData;
    private final String TAG = "PSerial";

    private UsbSerialPort sPort = null;

    boolean isStarted = false;
    private UsbSerialDriver driver;
    private SerialInputOutputManager.Listener mListener;
    private SerialInputOutputManager mSerialIoManager;
    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    String msg = "";
    private ReturnInterface mCallbackData;
    private ReturnInterface mCallbackConnected;

    public PSerial(AppRunner appRunner, int bauds) {
        super(appRunner);
        mBauds = bauds;
    }

    public void onConnected(ReturnInterface callbackConnected) {
        mCallbackConnected = callbackConnected;
    }

    @ProtoMethod(description = "starts serial", example = "")
    public void start() {
        getAppRunner().whatIsRunning.add(this);
        if (!isStarted) {

            //UsbSerialProber devices = UsbSerialProber.getDefaultProber();

            // Find all available drivers from attached devices.
            UsbManager manager = (UsbManager) getContext().getSystemService(Context.USB_SERVICE);

            //ProbeTable customTable = new ProbeTable();

            //customTable.addProduct(0x2012, 0x1f00, CdcAcmSerialDriver.class);
            //customTable.addProduct(0x2012, 0x1f00, UsbSerialDriver.class);
            //customTable.addProduct(0x1234, 0x0002, CdcAcmSerialDriver.class);

            //UsbSerialProber prober = new UsbSerialProber(customTable);
            //List<UsbSerialDriver> availableDrivers = prober.findAllDrivers(manager);

            List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
            if (availableDrivers.isEmpty()) {
                MLog.d(TAG, "no drivers found");
                return;
            }

            // Open a connection with the first available driver.
            UsbSerialDriver driver = availableDrivers.get(0);

            UsbDeviceConnection connection = manager.openDevice(driver.getDevice());
            if (connection == null) {
                // You probably need to call UsbManager.requestPermission(driver.getDevice(), ..)
                MLog.d(TAG, "no connection");

                return;
            }

            // Read some data! Most have just one port (port 0).
            List<UsbSerialPort> portList = driver.getPorts();

            sPort = portList.get(0);

            try {

                sPort.open(connection);
                sPort.setParameters(mBauds, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
                if (mCallbackConnected != null) {
                    ReturnObject o = new ReturnObject();
                    o.put("status", true);
                    mCallbackConnected.event(o);
                }

                mListener = new SerialInputOutputManager.Listener() {

                    @Override
                    public void onRunError(Exception e) {
                        MLog.d(TAG, "Runner stopped.");
                    }

                    @Override
                    public void onNewData(final byte[] data) {
                        final String readMsg = new String(data, 0, data.length);

                        //mHandler.post(new Runnable() {
                        //    @Override
                        //    public void run() {
                        //antes pasaba finalMsgReturn
                        //        callbackfn.event(readMsg);
                        //    }
                        //});

                        msg = msg + readMsg;
                        int newLineIndex = msg.indexOf('\n');
                        MLog.d(TAG, "index " + newLineIndex);
                        String msgReturn = "";
                        if (newLineIndex != -1) {
                            msgReturn = msg.substring(0, newLineIndex);
                            msg = msg.substring(newLineIndex + 1);

                        }

                        MLog.d(TAG, msg);
                        if (msgReturn.trim().equals("") == false) {

                            final String finalMsgReturn = msgReturn;
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ReturnObject o = new ReturnObject();
                                    o.put("data", finalMsgReturn);
                                    if (mCallbackData != null) mCallbackData.event(o);
                                }
                            });
                        }


                    }
                };

                startIoManager();

                isStarted = true;

            } catch (IOException e) {
                MLog.e(TAG, "Error setting up device: " + e.getMessage() + e);
                if (mCallbackConnected != null) {
                    ReturnObject o = new ReturnObject();
                    o.put("status", false);
                    mCallbackConnected.event(o);
                }
                //mTitleTextView.setText("Error opening device: " + e.getMessage());
                try {
                    sPort.close();
                } catch (IOException e2) {
                    // Ignore.
                }
                sPort = null;
                return;
            }
            onDeviceStateChange();

        }

    }

    public PSerial onNewData(ReturnInterface cb) {
        mCallbackData = cb;

        return this;
    }

    private void stopIoManager() {
        if (mSerialIoManager != null) {
            MLog.i(TAG, "Stopping io manager ..");
            mSerialIoManager.stop();
            mSerialIoManager = null;
        }
    }

    private void startIoManager() {

        if (sPort != null) {
            MLog.i(TAG, "Starting io manager ..");
            mSerialIoManager = new SerialInputOutputManager(sPort, mListener);
            mExecutor.submit(mSerialIoManager);
        }
    }

    private void onDeviceStateChange() {
        stopIoManager();
        startIoManager();
    }

    @ProtoMethod(description = "stop serial", example = "")
    @ProtoMethodParam(params = {})
    public void stop() {
        if (isStarted) {
            isStarted = false;

            stopIoManager();
            if (sPort != null) {
                try {
                    sPort.close();
                } catch (IOException e) {
                    // Ignore.
                }
                sPort = null;
            }
        }
        ReturnObject o = new ReturnObject();
        o.put("status", false);
        mCallbackConnected.event(o);
    }

    @ProtoMethod(description = "sends commands to the serial")
    @ProtoMethodParam(params = {"data"})
    public void write(String data) {
        if (isStarted) {
            try {
                sPort.write(data.getBytes(), 1000);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //@ProtoMethod(description = "resumes serial")
    public void resume() {

    }

    //@ProtoMethod(description = "pause serial")
    public void pause() {

    }

    @Override
    public void __stop() {
        stop();
    }
}