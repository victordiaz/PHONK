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

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.util.ArrayList;

import io.phonk.runner.AppRunnerActivity;
import io.phonk.runner.base.utils.MLog;

public class MyLifecycleHandler implements Application.ActivityLifecycleCallbacks {

    private final String TAG = MyLifecycleHandler.class.getSimpleName();

    // I use four separate variables here. You can, of course, just use two and
    // increment/decrement them instead of using four and incrementing them all.
    private int resumed;
    private int paused;
    private int started;
    private int stopped;

    static final ArrayList<Activity> mRunningScripts = new ArrayList<>();

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        String activityName = activity.getClass().getSimpleName();
        MLog.d(TAG, activityName);

        if (activityName.equals(AppRunnerActivity.class.getSimpleName())) {
            MLog.d(TAG, "added");
            mRunningScripts.add(activity);
        }
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        String activityName = activity.getClass().getSimpleName();
        MLog.d(TAG, activityName);


    }

    @Override
    public void onActivityResumed(Activity activity) {
        ++resumed;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        ++paused;
        MLog.d("test", "application is in foreground: " + (resumed > paused));
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
        ++started;
    }

    @Override
    public void onActivityStopped(Activity activity) {
        ++stopped;
        MLog.d("test", "application is visible: " + (started > stopped));
    }

    // And these two public static functions
    public boolean isApplicationVisible() {
        return started > stopped;
    }

    public boolean isApplicationInForeground() {
        return resumed > paused;
    }

    public void closeAllScripts() {
        for (int i = 0; i < mRunningScripts.size(); i++) {
            Activity activity = mRunningScripts.get(0);
            if (activity != null) activity.finish();
        }
        mRunningScripts.clear();
    }

}
