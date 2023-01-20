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

package io.phonk.runner.apprunner.api.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import io.phonk.runner.apidoc.annotation.PhonkClass;
import io.phonk.runner.apidoc.annotation.PhonkMethod;
import io.phonk.runner.apidoc.annotation.PhonkMethodParam;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.api.common.ReturnInterface;
import io.phonk.runner.apprunner.api.other.WhatIsRunningInterface;
import io.phonk.runner.apprunner.interpreter.AppRunnerInterpreter;

@PhonkClass
public abstract class PCustomSensorManager implements WhatIsRunningInterface {
    private final static String TAG = PCustomSensorManager.class.getSimpleName();
    protected final SensorManager mSensormanager;
    final AppRunner mAppRunner;
    protected Sensor sensor;
    protected SensorEventListener mListener;
    protected ReturnInterface mCallback;
    protected int speed = SensorManager.SENSOR_DELAY_FASTEST;
    protected int type = -1;
    protected boolean isEnabled = false;

    public PCustomSensorManager(AppRunner appRunner) {
        mAppRunner = appRunner;
        mSensormanager = (SensorManager) mAppRunner.getAppContext().getSystemService(Context.SENSOR_SERVICE);
    }

    @PhonkMethod(description = "Start the sensor", example = "")
    public void start() {
        if (isEnabled) return;
        if (!isAvailable()) {
            mAppRunner.pConsole.p_error(
                    AppRunnerInterpreter.RESULT_NOT_CAPABLE,
                    this.getClass().getSimpleName().substring(1)
            );
            return;
        }
        mAppRunner.whatIsRunning.add(this);
        sensor = mSensormanager.getDefaultSensor(type);
    }

    @PhonkMethod(description = "Check if the device has the sensor", example = "")
    public boolean isAvailable() {
        return mSensormanager.getDefaultSensor(type) != null;
    }

    @PhonkMethod(description = "Set the speed of the sensor 'slow', 'normal', 'fast'", example = "")
    @PhonkMethodParam(params = {"speed=['slow', 'normal', 'fast']"})
    public void sensorSpeed(String speed) {
        if (speed.equals("slow")) {
            this.speed = SensorManager.SENSOR_DELAY_UI;
        } else if (speed.equals("fast")) {
            this.speed = SensorManager.SENSOR_DELAY_FASTEST;
        } else {
            this.speed = SensorManager.SENSOR_DELAY_NORMAL;
        }
    }

    @PhonkMethod
    public float max() {
        return sensor.getMaximumRange();
    }

    @PhonkMethod
    public float power() {
        return sensor.getPower();
    }

    @PhonkMethod
    public float resolution() {
        return sensor.getResolution();
    }

    @PhonkMethod
    public Sensor info() {
        return mSensormanager.getDefaultSensor(type);
    }

    @PhonkMethod
    public abstract String units();

    public void __stop() {
        stop();
    }

    @PhonkMethod(description = "Stop the sensor", example = "")
    @PhonkMethodParam(params = {""})
    public void stop() {
        isEnabled = false;
        if (mListener != null) {
            mSensormanager.unregisterListener(mListener);
            mListener = null;
        }
    }
}
