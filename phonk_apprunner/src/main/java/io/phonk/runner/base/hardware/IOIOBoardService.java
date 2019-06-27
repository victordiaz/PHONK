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

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import io.phonk.runner.base.utils.MLog;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOService;

public class IOIOBoardService extends IOIOService {
    public static final int SET_CALLBACK = 1;
    protected static final String TAG = IOIOBoardService.class.getSimpleName();
    private HardwareCallback callback_;
    protected Boolean abort_ = false;
    // Binder given to clients
    private final IBinder mBinder = new IOIOServiceBinder();

    public class IOIOServiceBinder extends Binder {
        IOIOBoardService getService() {
            return IOIOBoardService.this;
        }
    }

    @Override
    protected IOIOLooper createIOIOLooper() {
        MLog.d(TAG, "createIOIOLooper");
        return new BaseIOIOLooper() {
            @Override
            protected void setup() throws ConnectionLostException, InterruptedException {
                MLog.d(TAG, "Setup in IOIOLooper");
                callback_.onConnect(ioio_);
                callback_.setup();
                // abort_ = (resp != null && resp != true);
            }

            @Override
            public void loop() throws ConnectionLostException, InterruptedException {
                if (abort_) {
                    this.disconnected();
                } else {
                    callback_.loop();
                    // abort_ = (resp != null && resp != true);
                    Thread.sleep(100);
                }
            }

            @Override
            public void disconnected() {
                super.disconnected();
                MLog.d("IOIOBoardService", "-----> Disconnecting <-----");
                ioio_.disconnect();
            }
        };
    }

    @Override
    public void onStart(Intent intent, int startId) {
        MLog.d(TAG, "onSTART");
        super.onStart(intent, startId);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    public void setCallback(HardwareCallback cb) {
        MLog.d(TAG, "setCallback");
        callback_ = cb;
    }

    public void start(Intent in) {
        MLog.d(TAG, "START WITH INTENT");
        startService(in);
    }
}
