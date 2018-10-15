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
import io.phonk.runner.api.sensors.PLocation;
import io.phonk.runner.api.sensors.PGyroscope;
import io.phonk.runner.api.sensors.PLightIntensity;
import io.phonk.runner.api.sensors.PMagnetic;
import io.phonk.runner.api.sensors.POrientation;
import io.phonk.runner.api.sensors.PPressure;
import io.phonk.runner.api.sensors.PProximity;
import io.phonk.runner.api.sensors.PStep;
import io.phonk.runner.apidoc.annotation.ProtoField;
import io.phonk.runner.apidoc.annotation.ProtoObject;
import io.phonk.runner.apprunner.AppRunner;


@ProtoObject
public class PSensors extends ProtoBase {

    @ProtoField
    public final PAccelerometer accelerometer;
    @ProtoField
    public final PGyroscope gyroscope;
    @ProtoField
    public final PLocation location;
    @ProtoField
    public final PLightIntensity light;
    @ProtoField
    public final PMagnetic magnetic;
    @ProtoField
    public final POrientation orientation;
    @ProtoField
    public final PPressure pressure;
    @ProtoField
    public final PProximity proximity;
    @ProtoField
    public final PStep stepDetector;

    public PSensors(AppRunner appRunner) {
        super(appRunner);

        accelerometer = new PAccelerometer(appRunner);
        gyroscope = new PGyroscope(appRunner);
        location = new PLocation(appRunner);
        light = new PLightIntensity(appRunner);
        magnetic = new PMagnetic(appRunner);
        orientation = new POrientation(appRunner);
        pressure = new PPressure(appRunner);
        proximity = new PProximity(appRunner);
        stepDetector = new PStep(appRunner);
    }

    public ReturnObject listAvailable() {
        ReturnObject r = new ReturnObject();
        r.put("accelerometer", accelerometer.isAvailable());
        r.put("gyroscope", gyroscope.isAvailable());
        r.put("location", location.isAvailable());
        r.put("light", light.isAvailable());
        r.put("magnetic", magnetic.isAvailable());
        r.put("orientation", orientation.isAvailable());
        r.put("pressure", pressure.isAvailable());
        r.put("proximity", proximity.isAvailable());
        r.put("step", stepDetector.isAvailable());

        return r;
    }

    @Override
    public void __stop() {

    }
}
