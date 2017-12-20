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

public class IOIOBoard extends HardwareBase {

    private static String TAG = IOIOBoard.class.getSimpleName();

    private final Context mContext;
    private IOIOBoardService service_;
    private Intent serviceIntent_;
    private Boolean serviceBound = false;
    protected IOIO ioio;

    private final ServiceConnection connection_ = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
            MLog.d(TAG, "onServiceDisconnected");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            IOIOBoardService.IOIOServiceBinder binder = (IOIOBoardService.IOIOServiceBinder) service;
            service_ = binder.getService();
            service_.setCallback(callback_);
            service_.start(serviceIntent_);
            serviceBound = true;
            MLog.d(TAG, "onServiceConnected");
        }
    };

    public IOIOBoard(Context context, HardwareCallback callback) {
        super(callback);
        mContext = context;
    }

    /**
     * Start To power on the board u can do this (/system/xbin/makr_poweron.sh):
     * <p/>
     * echo 43 > /sys/class/gpio/export echo out >
     * /sys/class/gpio/gpio43/direction echo 1 > /sys/class/gpio/gpio43/value
     */
    @Override
    public void powerOn() {
        //SysFs.write("/sys/class/gpio/export", "43");
        //SysFs.write("/sys/class/gpio/gpio43/direction", "out");
        //SysFs.write("/sys/class/gpio/gpio43/value", "1");

        MLog.d(TAG, "Setting up intent");
        serviceIntent_ = new Intent(mContext, IOIOBoardService.class);
        MLog.d(TAG, "Binding service...");
        mContext.bindService(serviceIntent_, connection_, Context.BIND_AUTO_CREATE);
        MLog.d(TAG, "Service bound with connection");
    }

    /**
     * Power off the board To power off the board u can do this
     * (/system/xbin/makr_poweroff.sh):
     * <p/>
     * echo 43 > /sys/class/gpio/export echo out >
     * /sys/class/gpio/gpio43/direction echo 0 > /sys/class/gpio/gpio43/value
     */
    @Override
    public void powerOff() {
        if (serviceBound) {
            MLog.d(TAG, "Aborting thread...");
            service_.stopSelf();
            mContext.unbindService(connection_);
            serviceBound = false;
            service_ = null;
        }

        /*
        SysFs.write("/sys/class/gpio/export", "43");
        SysFs.write("/sys/class/gpio/gpio43/direction", "out");
        SysFs.write("/sys/class/gpio/gpio43/value", "0");
        */
    }

    public void __stop() {
        MLog.d(TAG, "IOIOBoard stop called");
        // powerOff();
        if (serviceIntent_ != null) {
            mContext.stopService(serviceIntent_);
        }
    }

}
