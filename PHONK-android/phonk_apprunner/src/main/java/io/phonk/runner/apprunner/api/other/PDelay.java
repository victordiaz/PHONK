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

package io.phonk.runner.apprunner.api.other;

import android.os.Handler;

import io.phonk.runner.apidoc.annotation.PhonkClass;
import io.phonk.runner.apidoc.annotation.PhonkMethod;
import io.phonk.runner.apprunner.AppRunner;

@PhonkClass
public class PDelay implements WhatIsRunningInterface {
    private final AppRunner mAppRunner;
    private final int delay;
    private final DelayCB mCallbackfn;
    Runnable task;
    private final Handler handler;
    boolean mCancelJob = false;

    public interface DelayCB {
        void event();
    }

    public PDelay(AppRunner appRunner, final int delay, final DelayCB callbackkfn) {
        mAppRunner = appRunner;
        handler = new Handler();

        mCallbackfn = callbackkfn;
        this.delay = delay;

        Runnable task = new Runnable() {
            @Override
            public void run() {
                if (mCancelJob) return;

                callbackkfn.event();
                handler.removeCallbacks(this);
            }
        };
        handler.postDelayed(task, delay);

        mAppRunner.whatIsRunning.add(this);
    }

    @PhonkMethod(description = "Stop the timer", example = "")
    public PDelay stop() {
        handler.removeCallbacks(task);
        mCancelJob = true;

        return this;
    }

    public void __stop() {
        stop();
    }

}