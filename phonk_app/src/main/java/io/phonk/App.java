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

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import io.phonk.gui.connectionInfo.EventManager;
import io.phonk.runner.base.utils.TimerUtils;

public class App extends MultiDexApplication {

    public static MyLifecycleHandler myLifecycleHandler;
    public EventManager eventManager;

    @Override
    public void onCreate() {
        super.onCreate();

        MultiDex.install(this);

        TimerUtils.start();
        TimerUtils.stamp("start");
        TimerUtils.stamp("MultiDex.install");

        myLifecycleHandler = new MyLifecycleHandler();
        TimerUtils.stamp("MyLifecycleHandler");

        registerActivityLifecycleCallbacks(myLifecycleHandler);
        TimerUtils.stamp("registerActivityLifecycleCallbacks");

        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof PhonkExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new PhonkExceptionHandler());
        }

        eventManager = new EventManager(getApplicationContext());

        // EmojiCompat.Config config = new BundledEmojiCompatConfig(this);
        // EmojiCompat.init(config);
    }
}
