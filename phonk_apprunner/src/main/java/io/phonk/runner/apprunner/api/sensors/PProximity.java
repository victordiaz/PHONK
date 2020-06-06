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

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import io.phonk.runner.apidoc.annotation.PhonkClass;
import io.phonk.runner.apidoc.annotation.PhonkMethod;
import io.phonk.runner.apidoc.annotation.PhonkMethodParam;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.api.common.ReturnInterface;
import io.phonk.runner.apprunner.api.common.ReturnObject;
import io.phonk.runner.apprunner.api.other.WhatIsRunningInterface;

@PhonkClass
public class PProximity extends PCustomSensorManager implements WhatIsRunningInterface {
    private final static String TAG = PProximity.class.getSimpleName();

    public PProximity(AppRunner appRunner) {
        super(appRunner);

        type = Sensor.TYPE_PROXIMITY;
    }


    @PhonkMethod(description = "Start the proximity sensor. Returns a proximity value. It might differ per device", example = "")
    @PhonkMethodParam(params = {"function(proximity)"})
    public void start() {
        super.start();

        mListener = new SensorEventListener() {

            @Override
            public void onSensorChanged(SensorEvent event) {
                ReturnObject r = new ReturnObject();
                r.put("distance", event.values[0]);
                mCallback.event(r);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                switch (accuracy) {
                    case SensorManager.SENSOR_STATUS_UNRELIABLE:
                        break;
                    case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
                        break;
                    case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                        break;
                    case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
                        break;
                }
            }

        };

        isEnabled = mSensormanager.registerListener(mListener, sensor, speed);
    }

    @Override
    public String units() {
        return "cm";
    }

    @PhonkMethod(description = "Start the proximity sensor. Returns x, y, z", example = "")
    @PhonkMethodParam(params = {"function(x, y, z)"})
    public PProximity onChange(final ReturnInterface callbackfn) {
        mCallback = callbackfn;

        return this;
    }

}
