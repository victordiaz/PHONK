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

package io.phonk.appinterpreter;

import android.content.Context;

import java.util.HashMap;

import io.phonk.runner.apprunner.AppRunner;


public class AppRunnerCustom extends AppRunner {

    public PhonkApp phonkApp;

    public AppRunnerCustom(Context context) {
        super(context);
    }

    public AppRunnerCustom initDefaultObjects(HashMap<String, Object> settings) {
        phonkApp = new PhonkApp(this);
        return (AppRunnerCustom) super.initDefaultObjects(settings);
    }

    public AppRunnerCustom initInterpreter() {
        super.initInterpreter();
        // interp.addJavaObjectToJs("phonkApp", phonkApp);

        return this;
    }
}
