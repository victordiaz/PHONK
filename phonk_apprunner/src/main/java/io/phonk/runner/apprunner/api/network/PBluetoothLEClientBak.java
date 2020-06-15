/*
 * Part of Phonk http://www.phonk.io
 * A prototyping platform for Android devices
 *
 * Copyright (C) 2013 - 2017 Victor Diaz Barrales @victordiaz (Protocoder)
 * Copyright (C) 2017 - Victor Diaz Barrales @victordiaz (Phonk)
 *
 * Phonk is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Phonk is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Phonk. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package io.phonk.runner.apprunner.api.network;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;

import java.util.List;
import java.util.Queue;
import java.util.UUID;

import io.phonk.runner.apidoc.annotation.PhonkClass;
import io.phonk.runner.apidoc.annotation.PhonkMethod;
import io.phonk.runner.apidoc.annotation.PhonkMethodParam;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.api.ProtoBase;
import io.phonk.runner.apprunner.api.common.ReturnInterface;
import io.phonk.runner.apprunner.api.common.ReturnObject;
import io.phonk.runner.apprunner.api.other.WhatIsRunningInterface;
import io.phonk.runner.apprunner.interpreter.PhonkNativeArray;
import io.phonk.runner.base.utils.MLog;

@SuppressLint("NewApi")
@PhonkClass
public class PBluetoothLEClientBak extends ProtoBase implements WhatIsRunningInterface {
    private Context mContext;
    private final AppRunner mAppRunner;

    private final static String DISCONNECTED = "disconnected";
    private final static String CONNECTING = "connecting";
    private final static String CONNECTED = "connected";
    private final static String DISCONNECTING = "disconnecting";

    private BluetoothAdapter mBleAdapter;
    private BluetoothGatt mGatt;

    // private Boolean connected = false;
    private ReturnInterface mCallbackGattConnection;
    private ReturnInterface mCallbackServices;
    private ReturnInterface mCallbackData;

    private String mConnectionStatus;

    public PBluetoothLEClientBak(AppRunner appRunner, BluetoothAdapter bleAdapter) {
        super(appRunner);
        mAppRunner = appRunner;
        mContext = appRunner.getAppContext();
        mBleAdapter = bleAdapter;
        mConnectionStatus = DISCONNECTED;
    }

    @PhonkMethod(description = "Connect to mContext bluetooth device using the mac address", example = "")
    @PhonkMethodParam(params = {"mac", "function(data)"})
    public void connectGatt(String mac) {
    }

    // Connect to a new Device
    public void connectGatt(String address, boolean autoConnect) {
        BluetoothDevice currentDevice = mBleAdapter.getRemoteDevice(address);
        mGatt = currentDevice.connectGatt(mContext, autoConnect, mbluetoothListener);
        MLog.d(TAG, "connecting to " + address);
    }

    @PhonkMethod(description = "Disconnect the bluetooth", example = "")
    @PhonkMethodParam(params = {""})
    public void disconnectGatt() {
        if (!mConnectionStatus.equals(DISCONNECTED)) {
            MLog.d(TAG, "disconnecting");
            mGatt.close();
        }
    }

    // here we get services and characteristics
    public void discoverServices() {
        mGatt.discoverServices();
    }

    @PhonkMethod(description = "Write value to characteristic", example = "")
    @PhonkMethodParam(params = {"uuid", "value"})
    public void writeCharacteristic(String uuid, Object value) {

    }

    @PhonkMethod(description = "Read value from characteristic", example = "")
    @PhonkMethodParam(params = {"uuid"})
    public void readCharacteristic(String uuid) {

    }

    public void listenToCharacteristic(String UUIDService, String UUIDCharacteristic) {
        MLog.d(TAG, "LISTENING...");

        BluetoothGattService service = mGatt.getService(UUID.fromString(UUIDService));
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(UUIDCharacteristic));

        for (BluetoothGattDescriptor bluetoothGattDescriptor : characteristic.getDescriptors()) {
            bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mGatt.writeDescriptor(bluetoothGattDescriptor);
        }
        mGatt.setCharacteristicNotification(characteristic, true);
    }

    @PhonkMethod(description = "Enable/Disable the bluetooth adapter", example = "")
    @PhonkMethodParam(params = {"boolean"})
    public boolean isConnected() {
        return mConnectionStatus == CONNECTED;
    }

    @Override
    public void __stop() {
        disconnectGatt();
    }


    /***********************************************************************************
     * IMPL
     */


    public PBluetoothLEClientBak onGattConnectionChanged(ReturnInterface callbackGattConnection) {
        mCallbackGattConnection = callbackGattConnection;
        return this;
    }

    public PBluetoothLEClientBak onServicesChanged(ReturnInterface callbackServices) {
        mCallbackServices = callbackServices;
        return this;
    }

    public PBluetoothLEClientBak onNewData(ReturnInterface callbackData) {
        mCallbackData = callbackData;
        return this;
    }

    // Callback that controls Connection State and Characteristic State
    private BluetoothGattCallback mbluetoothListener = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

            if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                mConnectionStatus = DISCONNECTED;
            } else if (newState == BluetoothProfile.STATE_CONNECTING) {
                mConnectionStatus = CONNECTING;
            } else if (newState == BluetoothProfile.STATE_CONNECTED) {
                mConnectionStatus = CONNECTED;
            } else if (newState == BluetoothProfile.STATE_DISCONNECTING) {
                mConnectionStatus = DISCONNECTING;
            }

            MLog.d(TAG, "connected " + mConnectionStatus);

            if (mCallbackGattConnection != null) {
                ReturnObject o = new ReturnObject();
                o.put("status", mConnectionStatus);
                o.put("gatt", gatt);
                mCallbackGattConnection.event(o);
            }
        }

        @Override
        // New services discovered
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            MLog.d(TAG, "onServicesDiscovered " + gatt + " " + status);
            ReturnObject ret = new ReturnObject();

            if (status == BluetoothGatt.GATT_SUCCESS) {
                List<BluetoothGattService> services = gatt.getServices();
                PhonkNativeArray retServicesArray = new PhonkNativeArray(services.size());
                ret.put("services", retServicesArray);

                int countServices = 0;
                for (BluetoothGattService service : services) {
                    MLog.d(TAG, "service " + service.getUuid() + " " + service.getType());

                    ReturnObject retService = new ReturnObject();
                    retService.put("uuid", service.getUuid());
                    retService.put("type", service.getType());
                    retServicesArray.addPE(countServices++, retService);

                    List<BluetoothGattCharacteristic> gattCharacteristic = service.getCharacteristics();
                    PhonkNativeArray retCharArray = new PhonkNativeArray(gattCharacteristic.size());
                    retService.put("characteristics", retCharArray);

                    int counterCharacteristics = 0;
                    for (BluetoothGattCharacteristic characteristic : gattCharacteristic) {
                        MLog.d(TAG, "Characteristics" + characteristic.getUuid() + " " + characteristic.getProperties() + " " + characteristic.getWriteType() + " " + characteristic.getValue());
                        gatt.readCharacteristic(characteristic);

                        ReturnObject retChar = new ReturnObject();
                        retChar.put("uuid", characteristic.getUuid());
                        retChar.put("writeType", characteristic.getWriteType());
                        /*
                        retChar.put("descriptors", characteristic.getDescriptors());
                        */
                        retCharArray.addPE(counterCharacteristics++, retChar);
                    }

                }

                if (mCallbackServices != null) {
                    mCallbackServices.event(ret);
                }
            } else {
                MLog.d(TAG, "onServicesDiscovered received: " + status);
            }
        }

        // Result of a characteristic read operation
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            MLog.d(TAG, "charRead 1" + " " + status);

            if (status == BluetoothGatt.GATT_SUCCESS) {
                short lsb = (short) (characteristic.getValue()[0] & 0xff);
                String val = String.valueOf(lsb);
                MLog.d(TAG, "charRead2" + " " + val);
                MLog.d(TAG, "charRead3 " + characteristic.getValue());
                MLog.d(TAG, "charRead5 " + characteristic.getStringValue(0));
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);

            MLog.d(TAG, "characteristicChanged " + characteristic.getUuid() + " " + characteristic.getValue() + " " + characteristic.getStringValue(0));

            if (mCallbackData != null) {
                ReturnObject r = new ReturnObject();
                r.put("valueByte", characteristic.getValue());
                r.put("uuid", characteristic.getUuid());
                r.put("value", characteristic.getStringValue(0));
                mCallbackData.event(r);
            }
        }

        /*
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            byte[] data = characteristic.getValue();

            boolean mFirstPacket = (data[0] & 0x80) == 0x80;
            int mMessageCount = (((data[0] & 0x60) >> 5));
            int mPendingCount = (data[0] & 0x1f);
            byte[] mPacket = data;
            ByteBuffer buffer = ByteBuffer.allocate(2);
            buffer.put(data[5]);
            buffer.put(data[6]);
            byte[] data2 = buffer.array();

            MLog.d(TAG, "Characteristic changed." + new String(data2));

            if (mCallbackData != null) {
                ReturnObject o = new ReturnObject();
                o.put("characteristicChanged", new String(data2));
                mCallbackData.event(o);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            MLog.i(TAG, "Service Discovered");

            ReturnObject o = new ReturnObject();
            o.put("services", status);
            mCallbackData.event(o);
        }
        */
    };
}
