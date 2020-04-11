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

package io.phonk.runner.api;

import io.phonk.runner.api.common.ReturnObject;
import io.phonk.runner.api.sensors.PAccelerometer;
import io.phonk.runner.api.sensors.PAmbientTemperature;
import io.phonk.runner.api.sensors.PGravity;
import io.phonk.runner.api.sensors.PGyroscope;
import io.phonk.runner.api.sensors.PHumidity;
import io.phonk.runner.api.sensors.PLightIntensity;
import io.phonk.runner.api.sensors.PLinearAcceleration;
import io.phonk.runner.api.sensors.PLocation;
import io.phonk.runner.api.sensors.PMagneticField;
import io.phonk.runner.api.sensors.POrientation;
import io.phonk.runner.api.sensors.PBarometer;
import io.phonk.runner.api.sensors.PProximity;
import io.phonk.runner.api.sensors.PRotationVector;
import io.phonk.runner.api.sensors.PStep;
import io.phonk.runner.apidoc.annotation.ProtoField;
import io.phonk.runner.apidoc.annotation.ProtoObject;
import io.phonk.runner.apprunner.AppRunner;


@ProtoObject
public class PSensors extends ProtoBase {

    @ProtoField
    public final PAccelerometer accelerometer;
    @ProtoField
    public final PLinearAcceleration linearAcceleration;
    @ProtoField
    public final PGravity gravity;
    @ProtoField
    public final PGyroscope gyroscope;
    @ProtoField
    public final PRotationVector rotationVector;
    @ProtoField
    public final PLocation location;
    @ProtoField
    public final PLightIntensity light;
    @ProtoField
    public final PMagneticField magneticField;
    @ProtoField
    public final POrientation orientation;
    @ProtoField
    public final PBarometer barometer;
    @ProtoField
    public final PProximity proximity;
    @ProtoField
    public final PStep stepDetector;
    @ProtoField
    public final PAmbientTemperature ambientTemperature;
    @ProtoField
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
