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

package io.phonk.runner.apprunner.api.other;

import android.os.Handler;

import java.util.ArrayList;

import io.phonk.runner.apidoc.annotation.PhonkClass;
import io.phonk.runner.apidoc.annotation.PhonkMethod;
import io.phonk.runner.apidoc.annotation.PhonkMethodParam;
import io.phonk.runner.apprunner.AppRunner;

@PhonkClass
public class PLooper implements WhatIsRunningInterface {
    private final AppRunner mAppRunner;
    private LooperCB mCallbackfn;
    Runnable task;
    private final Handler handler;
    ArrayList<Runnable> rl = new ArrayList<Runnable>();

    public int speed;
    boolean isLooping = false;

    // --------- Looper ---------//
    public interface LooperCB {
        void event();
    }

    public PLooper(AppRunner appRunner, final int speed, final LooperCB callbackkfn) {
        mAppRunner = appRunner;
        handler = new Handler();

        mCallbackfn = callbackkfn;
        this.speed = speed;

        task = new Runnable() {

            @Override
            public void run() {
                if (!isLooping) return;

                if (mCallbackfn != null) {
                    mCallbackfn.event();
                }

                handler.postDelayed(this, PLooper.this.speed);
            }
        };

        rl.add(task);

        mAppRunner.whatIsRunning.add(this);
    }

    public PLooper onLoop(LooperCB callbackfn) {
        mCallbackfn = callbackfn;

        return this;
    }

    @PhonkMethod(description = "Change the current time speed to a new one", example = "")
    @PhonkMethodParam(params = {"speed"})
    public PLooper speed(int speed) {
        if (!this.isLooping) return this;

        this.speed = speed;
        stop();
        start();

        return this;
    }

    /*
    @ProtoMethod(description = "Pause the looper", example = "")
    @ProtoMethodParam(params = {"boolean"})
    public PLooper pause(boolean paused) {
        if (paused) {
          this.isLooping = false;
        } else {
            this.isLooping = true;
            handler.postDelayed(task, speed);
        }
        return this;
    }
     */

    @PhonkMethod(description = "Stop the looper", example = "")
    public PLooper stop() {
        this.isLooping = false;
        handler.removeCallbacks(task);

        return this;
    }

    @PhonkMethod(description = "Start the looper", example = "")
    public PLooper start() {
        if (!this.isLooping) {
            this.isLooping = true;
            handler.post(task);
        }

        return this;
    }

    public boolean isLooping() {
        return isLooping;
    }

    public void __stop() {
        stop();
    }

}
