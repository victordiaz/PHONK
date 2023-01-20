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
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOService;

public class IOIOBoardService extends IOIOService {
    protected static final String TAG = IOIOBoardService.class.getSimpleName();
    protected final Boolean mAbort = false;
    // Binder given to clients
    private final IBinder mBinder = new IOIOServiceBinder();
    private IOIOBoard.HardwareCallback mCallback;

    @Override
    public void onStart(Intent intent, int startId) {
        MLog.d(TAG, "onStart");
        super.onStart(intent, startId);
    }

    @Override
    protected IOIOLooper createIOIOLooper() {
        MLog.d(TAG, "createIOIOLooper");
        return new BaseIOIOLooper() {
            @Override
            protected void setup() throws InterruptedException {
                MLog.d(TAG, "Setup in IOIOLooper " + mCallback + " " + ioio_);

                if (mCallback != null) {
                    mCallback.onConnect(ioio_);
                    mCallback.setup();
                }
            }

            @Override
            public void loop() throws InterruptedException {
                MLog.d(TAG, "loop " + mCallback);
                if (mAbort) {
                    this.disconnected();
                } else {
                    if (mCallback != null) mCallback.loop();
                    // abort_ = (resp != null && resp != true);
                    Thread.sleep(100);
                }
            }

            @Override
            public void disconnected() {
                super.disconnected();
                MLog.d(TAG, "disconnected");
                ioio_.disconnect();
            }
        };
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    public void setCallback(IOIOBoard.HardwareCallback cb) {
        MLog.d(TAG, "3 -> setCallback");
        mCallback = cb;
    }

    public void start(Intent in) {
        MLog.d(TAG, "start");
        startService(in);
    }

    public class IOIOServiceBinder extends Binder {
        IOIOBoardService getService() {
            return IOIOBoardService.this;
        }
    }
}
