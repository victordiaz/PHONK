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

package io.phonk.runner.apprunner.api;

import io.phonk.runner.apidoc.annotation.PhonkField;
import io.phonk.runner.apidoc.annotation.PhonkObject;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.api.common.ReturnObject;
import io.phonk.runner.apprunner.api.sensors.PAccelerometer;
import io.phonk.runner.apprunner.api.sensors.PAmbientTemperature;
import io.phonk.runner.apprunner.api.sensors.PBarometer;
import io.phonk.runner.apprunner.api.sensors.PGravity;
import io.phonk.runner.apprunner.api.sensors.PGyroscope;
import io.phonk.runner.apprunner.api.sensors.PHumidity;
import io.phonk.runner.apprunner.api.sensors.PLightIntensity;
import io.phonk.runner.apprunner.api.sensors.PLinearAcceleration;
import io.phonk.runner.apprunner.api.sensors.PLocation;
import io.phonk.runner.apprunner.api.sensors.PMagneticField;
import io.phonk.runner.apprunner.api.sensors.POrientation;
import io.phonk.runner.apprunner.api.sensors.PProximity;
import io.phonk.runner.apprunner.api.sensors.PRotationVector;
import io.phonk.runner.apprunner.api.sensors.PStep;


@PhonkObject
public class PSensors extends ProtoBase {

    @PhonkField
    public final PAccelerometer accelerometer;
    @PhonkField
    public final PLinearAcceleration linearAcceleration;
    @PhonkField
    public final PGravity gravity;
    @PhonkField
    public final PGyroscope gyroscope;
    @PhonkField
    public final PRotationVector rotationVector;
    @PhonkField
    public final PLocation location;
    @PhonkField
    public final PLightIntensity light;
    @PhonkField
    public final PMagneticField magneticField;
    @PhonkField
    public final POrientation orientation;
    @PhonkField
    public final PBarometer barometer;
    @PhonkField
    public final PProximity proximity;
    @PhonkField
    public final PStep stepDetector;
    @PhonkField
    public final PAmbientTemperature ambientTemperature;
    @PhonkField
    public final PHumidity humidity;

    public PSensors(AppRunner appRunner) {
        super(appRunner);

        accelerometer = new PAccelerometer(appRunner);
        linearAcceleration = new PLinearAcceleration(appRunner);
        gravity = new PGravity(appRunner);
        gyroscope = new PGyroscope(appRunner);
        rotationVector = new PRotationVector(appRunner);
        location = new PLocation(appRunner);
        light = new PLightIntensity(appRunner);
        magneticField = new PMagneticField(appRunner);
        orientation = new POrientation(appRunner);
        barometer = new PBarometer(appRunner);
        proximity = new PProximity(appRunner);
        stepDetector = new PStep(appRunner);
        ambientTemperature = new PAmbientTemperature(appRunner);
        humidity = new PHumidity(appRunner);
    }

    public ReturnObject listAvailable() {
        ReturnObject r = new ReturnObject();
        r.put("accelerometer", accelerometer.isAvailable());
        r.put("linearAcceleration", linearAcceleration.isAvailable());
        r.put("gravity", gravity.isAvailable());
        r.put("rotationVector", rotationVector.isAvailable());
        r.put("gyroscope", gyroscope.isAvailable());
        r.put("location", location.isAvailable());
        r.put("light", light.isAvailable());
        r.put("magneticField", magneticField.isAvailable());
        r.put("orientation", orientation.isAvailable());
        r.put("barometer", barometer.isAvailable());
        r.put("proximity", proximity.isAvailable());
        r.put("step", stepDetector.isAvailable());
        r.put("ambientTemperature", ambientTemperature.isAvailable());
        r.put("humidity", humidity.isAvailable());

        return r;
    }

    @Override
    public void __stop() {

    }
}
