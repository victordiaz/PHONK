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

package io.phonk.runner.apprunner.api.network;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import io.phonk.runner.apidoc.annotation.PhonkClass;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.api.ProtoBase;
import io.phonk.runner.apprunner.api.common.ReturnInterface;
import io.phonk.runner.apprunner.api.common.ReturnObject;
import io.phonk.runner.apprunner.interpreter.AppRunnerInterpreter;
import io.phonk.runner.base.utils.MLog;


/**
 * TODO
 */
@SuppressLint("NewApi")
@PhonkClass
public class PBluetoothLE extends ProtoBase {
    private Context mContext;
    private final AppRunner mAppRunner;

    public Handler mHandler;

    // private List<BluetoothDevice> mDevices;
    // private BluetoothDevice currentDevice;
    private BluetoothAdapter mBleAdapter;
    private ReturnInterface mCallbackScan;

    public PBluetoothLE(AppRunner appRunner) {
        super(appRunner);
        mAppRunner = appRunner;

        mContext = appRunner.getAppContext();
    }

    public PBluetoothLE start() {
        if (!getAppRunner().getAppContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            getAppRunner().pConsole.p_error(AppRunnerInterpreter.RESULT_NOT_CAPABLE, "Bluetooth Low Energy");
            return null;
        }

        mBleAdapter = ((BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        mAppRunner.whatIsRunning.add(this);
        mHandler = new Handler(Looper.getMainLooper());

        return this;
    }


    /*
     * Search devices
     */

    // Callback that control the Functionality of scan devices.
    private BluetoothAdapter.LeScanCallback mScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            MLog.d(TAG, "found device " + device.getAddress() + " " + rssi);

            ReturnObject o = new ReturnObject();
            String name = device.getName();
            if (name == null) name = "";

            o.put("name", name);
            o.put("mac", device.getAddress());
            o.put("rssi", rssi);
            o.put("device", device);

            mHandler.post(() -> {
                mCallbackScan.event(o);
            });
        }
    };

    // Scan Bluetooth Devices for the especific miliseconds
    public PBluetoothLE scan(final ReturnInterface callbackfn) {
        MLog.d(TAG, "Scanning Devices");
        mBleAdapter.startLeScan(this.mScanCallback);
        this.mCallbackScan = callbackfn;

        return this;
    }

    public PBluetoothLE stopScan() {
        if (mBleAdapter != null) mBleAdapter.stopLeScan(mScanCallback);
        return this;
    }

    public PBluetoothLEClient createClient() {
        return new PBluetoothLEClient(mAppRunner, mBleAdapter);
    }

    @Override
    public void __stop() {
        this.stopScan();
    }

}
