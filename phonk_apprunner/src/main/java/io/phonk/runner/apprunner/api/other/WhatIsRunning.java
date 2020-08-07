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

package io.phonk.runner.apprunner.api.other;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Vector;

import io.phonk.runner.base.utils.MLog;

public class WhatIsRunning {

    private static final String TAG = WhatIsRunning.class.getSimpleName();
    private Vector<Object> runners;

    public WhatIsRunning() {
        runners = new Vector<Object>();
        // MLog.d(TAG, "instancing WhatIsRunning...");
    }

    public void stopAll() {
        for (Object o : runners) {
            Method method = null;

            try {
                method = o.getClass().getMethod("__stop");
            } catch (SecurityException e) {
            } catch (NoSuchMethodException e) {
            }

            MLog.d(TAG, "stopping " + o.getClass().getCanonicalName() + " " + o + " " + method);

            try {
                method.invoke(o);
            } catch (IllegalArgumentException e) {
                MLog.d(TAG, "cannot stop 1");
            } catch (IllegalAccessException e) {
                MLog.d(TAG, "cannot stop 2");
            } catch (InvocationTargetException e) {
                MLog.d(TAG, "cannot stop 3");
                e.printStackTrace();
            } catch (Exception e) {
                MLog.d(TAG, "cannot stop 4");
                e.printStackTrace();
            }
        }
    }

    public void add(Object object) {
        // MLog.d(TAG, "adding: " + object.getClass().getCanonicalName());
        runners.add(object);
    }

    public void remove(Object object) {
        runners.remove(object);
    }
}
