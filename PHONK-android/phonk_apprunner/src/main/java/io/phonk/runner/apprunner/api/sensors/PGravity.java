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

package io.phonk.runner.apprunner.api.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import io.phonk.runner.apidoc.annotation.PhonkClass;
import io.phonk.runner.apidoc.annotation.PhonkMethod;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.api.common.ReturnInterface;
import io.phonk.runner.apprunner.api.common.ReturnObject;

@PhonkClass
public class PGravity extends PCustomSensorManager {
    private final static String TAG = PGravity.class.getSimpleName();

    public PGravity(AppRunner appRunner) {
        super(appRunner);

        type = Sensor.TYPE_GRAVITY;
    }

    public void start() {
        super.start();

        mListener = new SensorEventListener() {

            @Override
            public void onSensorChanged(SensorEvent event) {
                if (mCallback != null) {
                    ReturnObject r = new ReturnObject();
                    r.put("x", event.values[0]);
                    r.put("y", event.values[1]);
                    r.put("z", event.values[2]);
                    float force = (float) Math.sqrt(Math.pow(event.values[0], 2) + Math.pow(
                            event.values[1],
                            2
                    ) + Math.pow(event.values[2], 2));
                    r.put("force", force);
                    mCallback.event(r);
                }
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
        return "m/s^2";
    }

    @PhonkMethod
    public PGravity onChange(final ReturnInterface callbackfn) {
        mCallback = callbackfn;

        return this;
    }

}
