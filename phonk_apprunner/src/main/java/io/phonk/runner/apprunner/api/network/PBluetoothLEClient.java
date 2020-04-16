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
import java.util.UUID;

import io.phonk.runner.apprunner.api.ProtoBase;
import io.phonk.runner.apprunner.api.common.ReturnInterface;
import io.phonk.runner.apprunner.api.common.ReturnObject;
import io.phonk.runner.apprunner.api.other.PhonkNativeArray;
import io.phonk.runner.apprunner.api.other.WhatIsRunningInterface;
import io.phonk.runner.apidoc.annotation.ProtoMethod;
import io.phonk.runner.apidoc.annotation.ProtoMethodParam;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.base.utils.MLog;

@SuppressLint("NewApi")
public class PBluetoothLEClient extends ProtoBase implements WhatIsRunningInterface {
    private Context mContext;
    private final AppRunner mAppRunner;

    private final static int DISCONNECTED = 0;
    private final static int CONNECTING = 1;
    private final static int CONNECTED = 2;
    private final static int DISCONNECTING = 3;

    private BluetoothAdapter mBleAdapter;
    private BluetoothGatt mGatt;

    // private Boolean connected = false;
    private ReturnInterface mCallbackGattConnection;
    private ReturnInterface mCallbackServices;
    private ReturnInterface mCallbackData;

    private int status;

    public PBluetoothLEClient(AppRunner appRunner, BluetoothAdapter bleAdapter) {
        super(appRunner);
        mAppRunner = appRunner;
        mContext = appRunner.getAppContext();
        mBleAdapter = bleAdapter;
        status = DISCONNECTED;
    }

    @ProtoMethod(description = "Connect to mContext bluetooth device using the mac address", example = "")
    @ProtoMethodParam(params = {"mac", "function(data)"})
    public void connectGatt(String mac) {
    }

    // Connect to a new Device
    public void connectGatt(String address, boolean autoConnect) {
        BluetoothDevice currentDevice = mBleAdapter.getRemoteDevice(address);
        mGatt = currentDevice.connectGatt(mContext, autoConnect, mbluetoothListener);
        MLog.d(TAG, "connecting to " + address);
    }

    @ProtoMethod(description = "Disconnect the bluetooth", example = "")
    @ProtoMethodParam(params = {""})
    public void disconnectGatt() {
        if (status != DISCONNECTED) {
            mGatt.close();
        }
    }

    // here we get services and characteristics
    public void discoverServices() {
        mGatt.discoverServices();
    }

    @ProtoMethod(description = "Write value to characteristic", example = "")
    @ProtoMethodParam(params = {"uuid", "value"})
    public void writeCharacteristic(String uuid, Object value) {

    }

    @ProtoMethod(description = "Read value from characteristic", example = "")
    @ProtoMethodParam(params = {"uuid"})
    public void readCharacteristic(String uuid) {

    }

    public void listenCharacteristic(String UUIDService, String UUIDCharacteristic) {
        MLog.d(TAG, "LISTENING...");

        BluetoothGattService service = mGatt.getService(UUID.fromString(UUIDService));
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(UUIDCharacteristic));

        for (BluetoothGattDescriptor bluetoothGattDescriptor : characteristic.getDescriptors()) {
            bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mGatt.writeDescriptor(bluetoothGattDescriptor);
        }
        mGatt.setCharacteristicNotification(characteristic, true);
    }

    @ProtoMethod(description = "Enable/Disable the bluetooth adapter", example = "")
    @ProtoMethodParam(params = {"boolean"})
    public boolean isConnected() {
        return status == CONNECTED;
    }

    @Override
    public void __stop() {
        disconnectGatt();
    }


    /***********************************************************************************
     * IMPL
     */


    public PBluetoothLEClient onGattConnectionChanged(ReturnInterface callbackGattConnection) {
        mCallbackGattConnection = callbackGattConnection;
        return this;
    }

    public PBluetoothLEClient onServicesChanged(ReturnInterface callbackServices) {
        mCallbackServices = callbackServices;
        return this;
    }

    public PBluetoothLEClient onNewData(ReturnInterface callbackData) {
        mCallbackData = callbackData;
        return this;
    }

    // Callback that controls Connection State and Characteristic State
    private BluetoothGattCallback mbluetoothListener = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                status = DISCONNECTED;
            } else if (newState == BluetoothProfile.STATE_CONNECTING) {
                status = CONNECTING;
            } else if (newState == BluetoothProfile.STATE_CONNECTED) {
                status = CONNECTED;
            } else if (newState == BluetoothProfile.STATE_DISCONNECTING) {
                status = DISCONNECTING;
            }

            MLog.d(TAG, "connected " + status);

            if (mCallbackGattConnection != null) {
                ReturnObject o = new ReturnObject();
                o.put("connected", status);
                o.put("gatt", gatt);
                mCallbackGattConnection.event(o);
            }
        }

        @Override
        // New services discovered
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
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
                        retChar.put("descriptors", characteristic.getDescriptors());
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

        @Override
        // Result of a characteristic read operation
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            MLog.d(TAG, "charRead 1" + " " + status);

            if (status == BluetoothGatt.GATT_SUCCESS) {
                short lsb = (short)(characteristic.getValue()[0] & 0xff);
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


    /*
    // get the Current Services
    public List<BluetoothGattService> getServices() throws Exception {
        return mGatt.getServices();
    }
    */

    /*
    @ProtoMethod(description = "initialice a new Listener from a especific Characteristic")
    @ProtoMethodParam(params = {"uuidService", "uuidCharacteristic", "callBackNewData"})
    public void listenCharacteristic(String uuidService, String uuidCharaceristic, callBackNewData callback) {
        BluetoothGattService service = mGatt.getService(UUID.fromString(uuidService));

        BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(uuidCharaceristic));
        mGatt.setCharacteristicNotification(characteristic, true);
        for (BluetoothGattDescriptor bluetoothGattDescriptor : characteristic.getDescriptors()) {
            bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mGatt.writeDescriptor(bluetoothGattDescriptor);
        }
        this.mcallBackNewData = callback;
        MLog.i(TAG, "LISTENING...");
    }
    */
}
