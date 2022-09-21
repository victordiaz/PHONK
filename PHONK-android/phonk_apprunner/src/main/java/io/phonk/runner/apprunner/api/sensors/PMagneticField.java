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
public class PMagneticField extends PCustomSensorManager implements WhatIsRunningInterface {
    private final static String TAG = PMagneticField.class.getSimpleName();

    private ReturnInterface mCallbackMagneticChange;

    public PMagneticField(AppRunner appRunner) {
        super(appRunner);

        type = Sensor.TYPE_MAGNETIC_FIELD;
    }

    @PhonkMethod(description = "Start the magneticField sensor", example = "")
    @PhonkMethodParam(params = {"function(value)"})
    public void start() {
        super.start();

        mListener = new SensorEventListener() {

            @Override
            public void onSensorChanged(SensorEvent event) {
                ReturnObject r = new ReturnObject();
                r.put("x", event.values[0]);
                r.put("y", event.values[1]);
                r.put("z", event.values[2]);
                mCallbackMagneticChange.event(r);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                switch (accuracy) {
                    case SensorManager.SENSOR_STATUS_UNRELIABLE:
                    case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
                    case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                    case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
                        break;
                }
            }

        };

        isEnabled = mSensormanager.registerListener(mListener, sensor, speed);
    }

    @Override
    public String units() {
        return "uT";
    }


    @PhonkMethod(description = "Start the magneticField sensor. Returns x, y, z", example = "")
    @PhonkMethodParam(params = {"function(x, y, z)"})
    public PMagneticField onChange(final ReturnInterface callbackfn) {
        mCallbackMagneticChange = callbackfn;

        return this;
    }


}
