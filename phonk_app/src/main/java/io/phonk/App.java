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

package io.phonk;

import android.app.Application;
import android.support.multidex.MultiDex;

import io.phonk.gui.settings.PhonkSettings;
import io.phonk.helpers.Timer2;

public class App extends Application {

    public static MyLifecycleHandler myLifecycleHandler;

    @Override
    public void onCreate() {
        super.onCreate();

        MultiDex.install(this);

        Timer2.start();
        Timer2.stamp("start");
        Timer2.stamp("MultiDex.install");

        myLifecycleHandler = new MyLifecycleHandler();
        Timer2.stamp("MyLifecycleHandler");

        registerActivityLifecycleCallbacks(myLifecycleHandler);
        Timer2.stamp("registerActivityLifecycleCallbacks");

        // Fabric.with(this, new Crashlytics());

        /*
        final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .logger(new DefaultLogger(Log.VERBOSE))
                .debuggable(true)
                .build();

        Fabric.with(fabric);
        */

        if(!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(PhonkSettings.getLogsFolder()));
        }
    }
}
