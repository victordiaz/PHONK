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

package io.phonk.runner.api.other;

import io.phonk.runner.base.utils.MLog;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Vector;

public class WhatIsRunning {

    private static final String TAG = WhatIsRunning.class.getSimpleName();
    private WhatIsRunning instance;
    private Vector<Object> runners;

    public WhatIsRunning() {
        runners = new Vector<Object>();
    }

    public void stopAll() {

        for (Object o : runners) {
            // MLog.d(TAG, "list " + o.getClass().getCanonicalName());
        }

        for (Object o : runners) {
            Method method = null;

            try {
                method = o.getClass().getMethod("__stop");
            } catch (SecurityException e) {
            } catch (NoSuchMethodException e) {
            }

            MLog.d(TAG, "stopping " + o.getClass().getCanonicalName() + " " + o + " " + method);

            try {
                //if (method !== null) {
                    method.invoke(o);
                //}
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            }
        }
    }

    public void add(Object object) {
        runners.add(object);
    }

    public void remove(Object object) {
        runners.remove(object);
    }
}
