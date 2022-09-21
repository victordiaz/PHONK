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


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

import org.mozilla.javascript.NativeArray;

import java.util.Set;
import java.util.UUID;

import io.phonk.runner.apidoc.annotation.PhonkClass;
import io.phonk.runner.apidoc.annotation.PhonkMethod;
import io.phonk.runner.apidoc.annotation.PhonkMethodParam;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.api.ProtoBase;
import io.phonk.runner.apprunner.api.common.ReturnInterface;
import io.phonk.runner.apprunner.api.common.ReturnObject;
import io.phonk.runner.apprunner.api.other.WhatIsRunningInterface;
import io.phonk.runner.apprunner.interpreter.AppRunnerInterpreter;
import io.phonk.runner.apprunner.interpreter.PhonkNativeArray;
import io.phonk.runner.base.utils.MLog;

@PhonkClass
public class PBluetooth extends ProtoBase implements WhatIsRunningInterface {

    protected static final UUID UUID_SPP = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    protected BluetoothAdapter mAdapter;
    private boolean mBtStarted = false;
    public static final int REQUEST_ENABLE_BT = 2;

    // interface to get info from the activity when requesting to enable the bluetooth adapter
    public interface onBluetoothListener {
        void onActivityResult(int requestCode, int resultCode, Intent data);
    }

    public PBluetooth(AppRunner appRunner) {
        super(appRunner);
    }

    @PhonkMethod(description = "Start the bluetooth adapter", example = "")
    @PhonkMethodParam(params = {""})
    public PBluetooth start() {
        // MLog.d(TAG, "Bluetooth is started: " + mBtStarted);

        mAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mAdapter == null) {
            getAppRunner().pConsole.p_error(AppRunnerInterpreter.RESULT_NOT_CAPABLE, "Bluetooth");
            return null;
        }

        // try to start the bluetooth if not enabled
        if (!mAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (getActivity() != null) {
                getActivity().startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            } else {
                MLog.d(TAG, "you must enabled bluetooth before");
            }
        } else {
            MLog.d(TAG, "BT enabled");
            mBtStarted = true;
        }

        // we get the result from the activity
        if (getActivity() != null) {
            MLog.d(TAG, "Prompt bluetooth Dialog in a Activity");

            getActivity().addBluetoothListener(new onBluetoothListener() {
                @Override
                public void onActivityResult(int requestCode, int resultCode, Intent data) {
                    onActivityResult(requestCode, resultCode, data);

                    // if OK bt is already enabled
                    if (requestCode == REQUEST_ENABLE_BT) {
                        if (resultCode == Activity.RESULT_OK) {
                            MLog.d(TAG, "enabling BT");
                            mBtStarted = true;
                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), "BT not enabled :(", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        } else {
            MLog.d(TAG, "not showing UI since running BT in a service");
        }

        getAppRunner().whatIsRunning.add(this);

        return this;
    }

    @PhonkMethod(description = "")
    @PhonkMethodParam(params = {""})
    public PBluetoothClient createClient() {
        start();
        return new PBluetoothClient(this, getAppRunner());
    }

    @PhonkMethod(description = "")
    @PhonkMethodParam(params = {""})
    public PBluetoothServer createServer(String name) {
        start();

        return new PBluetoothServer(this, getAppRunner(), name);
    }

    @PhonkMethod(description = "Scan bluetooth networks. Gives back the name, mac and signal strength", example = "")
    @PhonkMethodParam(params = {"function(name, macAddress, strength)"})
    public void scanNetworks(final ReturnInterface callbackfn) {
        MLog.d(TAG, "scanNetworks");
        start();

        mAdapter.startDiscovery();
        BroadcastReceiver mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);

                    ReturnObject o = new ReturnObject();
                    String name = device.getName();
                    if (name == null) name = "";

                    o.put("name", name);
                    o.put("mac", device.getAddress());
                    o.put("rssi", rssi);
                    callbackfn.event(o);
                }
            }
        };

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getContext().registerReceiver(mReceiver, filter);

    }

    @PhonkMethod(description = "Send bluetooth serial message", example = "")
    @PhonkMethodParam(params = {"string"})
    public NativeArray getBondedDevices() {
        start();

        Set<BluetoothDevice> listDevices = mAdapter.getBondedDevices();
        MLog.d(TAG, "listDevices " + listDevices);
        int listSize = listDevices.size();
        PhonkNativeArray array = new PhonkNativeArray(listSize);
        MLog.d(TAG, "array " + array);


        int counter = 0;
        for (BluetoothDevice b : listDevices) {
            MLog.d(TAG, "bt " + b);

            ReturnObject btDevice = new ReturnObject();
            btDevice.put("name", b.getName());
            btDevice.put("mac", b.getAddress());

            array.addPE(counter++, btDevice);
        }

        return array;
    }

    @PhonkMethod(description = "Enable the bluetooth adapter", example = "")
    @PhonkMethodParam(params = {})
    public void enable() {
        start();
    }

    @PhonkMethod(description = "Disable the bluetooth adapter", example = "")
    @PhonkMethodParam(params = {})
    public void disable() {
        start();
        mAdapter.disable();
    }

    @Override
    public void __stop() {
    }

    protected BluetoothAdapter getAdapter() {
        return mAdapter;
    }


}
