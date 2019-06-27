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

package io.phonk.runner.api.other;

import android.os.Handler;

import java.util.ArrayList;

import io.phonk.runner.apidoc.annotation.ProtoMethod;
import io.phonk.runner.apidoc.annotation.ProtoMethodParam;
import io.phonk.runner.apprunner.AppRunner;

public class PLooper implements WhatIsRunningInterface {
    private final AppRunner mAppRunner;
    private LooperCB mCallbackfn;
    Runnable task;
    private final Handler handler;
    ArrayList<Runnable> rl = new ArrayList<Runnable>();

    public int speed;
    boolean paused = false;
    boolean isLooping = false;

    // --------- Looper ---------//
    public interface LooperCB {
        void event();
    }

    public PLooper(AppRunner appRunner, final int duration, final LooperCB callbackkfn) {
        mAppRunner = appRunner;
        handler = new Handler();

        mCallbackfn = callbackkfn;
        speed = duration;

        task = new Runnable() {

            @Override
            public void run() {
                if (mCallbackfn != null) {
                    mCallbackfn.event();
                }

                if (!paused) {
                    handler.postDelayed(this, speed);
                }
            }
        };

        rl.add(task);

        //TODO enable
        mAppRunner.whatIsRunning.add(this);
    }

    public PLooper onLoop(LooperCB callbackfn) {
        mCallbackfn = callbackfn;

        return this;
    }

    @ProtoMethod(description = "Change the current time speed to a new one", example = "")
    @ProtoMethodParam(params = {"duration"})
    public PLooper speed(int duration) {
        this.speed = duration;
        if (duration < this.speed) {
            stop();
            start();
        }
        return this;
    }

    @ProtoMethod(description = "Pause the looper", example = "")
    @ProtoMethodParam(params = {"boolean"})
    public PLooper pause(boolean b) {
        this.paused = b;
        this.isLooping = false;
        if (b == false) {
            handler.postDelayed(task, speed);
        }

        return this;
    }

    @ProtoMethod(description = "Stop the looper", example = "")
    public PLooper stop() {
        this.isLooping = false;
        handler.removeCallbacks(task);

        return this;
    }

    @ProtoMethod(description = "Start the looper", example = "")
    public PLooper start() {
        this.isLooping = true;
        handler.post(task);

        return this;
    }

    public boolean isLooping() {
        return isLooping;
    }

    public void __stop() {
        stop();
    }

}