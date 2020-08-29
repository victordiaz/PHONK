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

package io.phonk.runner.base.hardware;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import io.phonk.runner.base.utils.MLog;
import ioio.lib.api.IOIO;

public class IOIOBoard {

    private static String TAG = IOIOBoard.class.getSimpleName();

    private final Context mContext;
    private IOIOBoardService mIOIOService;
    private Intent mServiceIntent;
    private Boolean mServiceBound = false;
    protected HardwareCallback mHardwareCallback;

    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            MLog.d(TAG, "onServiceDisconnected");
            mServiceBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MLog.d(TAG, "2 --> onServiceConnected");
            IOIOBoardService.IOIOServiceBinder binder = (IOIOBoardService.IOIOServiceBinder) service;
            mIOIOService = binder.getService();
            mIOIOService.setCallback(mHardwareCallback);
            mIOIOService.start(mServiceIntent);
            mServiceBound = true;
        }
    };

    public IOIOBoard(Context context, HardwareCallback callback) {
        mContext = context;
        mHardwareCallback = callback;
    }

    public void powerOn() {
        MLog.d(TAG, "1 --> Binding service...");
        mServiceIntent = new Intent(mContext, IOIOBoardService.class);
        mContext.bindService(mServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

    public void powerOff() {
        if (mServiceBound) {
            MLog.d(TAG, "Aborting thread...");
            mIOIOService.stopSelf();
            mContext.unbindService(mConnection);
            mServiceBound = false;
            mIOIOService = null;
        }
    }

    public void __stop() {
        MLog.d(TAG, "IOIOBoard stop called");
        powerOff();
        if (mServiceIntent != null) {
            mContext.stopService(mServiceIntent);
        }
    }

    public interface HardwareCallback {
        void onConnect(Object obj);

        void setup();

        void loop();

        void onComplete();
    }
}
