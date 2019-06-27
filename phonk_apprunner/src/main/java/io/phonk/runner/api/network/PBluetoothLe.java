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

package io.phonk.runner.api.network;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Handler;

import java.nio.ByteBuffer;

import io.phonk.runner.api.ProtoBase;
import io.phonk.runner.api.common.ReturnInterface;
import io.phonk.runner.api.common.ReturnObject;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.base.utils.MLog;


/**
 * TODO
 */
@SuppressLint("NewApi")
public class PBluetoothLe extends ProtoBase {

    private Context mContext;

    // private List<BluetoothDevice> mDevices;
    // private BluetoothDevice currentDevice;
    private BluetoothAdapter mBleAdapter;
    private BluetoothGatt mGatt;

    private static Long SCAN_TIMEOUT = 10000L;

    // private Boolean connected = false;
    private ReturnInterface mCallbackData;
    private ReturnInterface mCallbackStatus;


    public PBluetoothLe(AppRunner appRunner) {
        super(appRunner);

        mContext = appRunner.getAppContext();
        // mDevices = new ArrayList<BluetoothDevice>();
        mBleAdapter = ((BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        appRunner.whatIsRunning.add(this);

        MLog.d(TAG, "Initializated");
    }

    public PBluetoothLe onStatus(ReturnInterface callbackStatus) {
        mCallbackStatus = callbackStatus;
        return this;
    }

    public PBluetoothLe onNewData(ReturnInterface callbackfn) {
        mCallbackData = callbackfn;

        return this;
    }


    // Callback that controls Connection State and Characteristic State
    private BluetoothGattCallback mbluetoothListener = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (mCallbackStatus != null) {
                // connected = (status == 0);
                // mGatt.discoverServices();

                MLog.d(TAG, "connected");
                ReturnObject o = new ReturnObject();
                o.put("connected", status);
                mCallbackData.event(o);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            if (mCallbackData != null) {
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
    };


    /*
     * Search devices
     */

    // Callback that control the Functionality of scan devices.
    private BluetoothAdapter.LeScanCallback mScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            MLog.d("found device " + device.getAddress() + " " + rssi);
        }
    };

    // Runnable that stop the Scan Mode
    private Runnable SearchDevices = new Runnable() {
        @Override
        public void run() {
            mBleAdapter.stopLeScan(mScanCallback);
        }
    };

    // Scan Bluetooth Devices for the especific miliseconds
    public PBluetoothLe scan(Long milis) {
        MLog.d(TAG, "Scanning Devices");
        Handler handler = new Handler();
        mBleAdapter.startLeScan(this.mScanCallback);
        handler.postDelayed(SearchDevices, milis);

        return this;
    }


    // Connect to a new Device
    public void connect(String address) {
        BluetoothDevice currentDevice = mBleAdapter.getRemoteDevice(address);
        mGatt = currentDevice.connectGatt(mContext, true, mbluetoothListener);
        MLog.i(TAG, "connecting to " + address);
    }

    /*
    // Show if is connected
    public Boolean isConnected() {
        return connected;
    }
    */

    /*
    // Disconnect from the current device
    public void disconnect() {
        this.mGatt.close();
        this.connected = false;
    }
    */

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

    /*
    public BluetoothDevice getCurrentDevice() {
        return this.currentDevice;
    }
    */

    @Override
    public void __stop() {
        // if (connected) {
        //     mGatt.close();
        // }
    }

}
