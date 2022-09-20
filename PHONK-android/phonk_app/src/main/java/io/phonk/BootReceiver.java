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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import io.phonk.gui.settings.UserPreferences;
import io.phonk.runner.AppRunnerActivity;
import io.phonk.runner.base.models.Project;
import io.phonk.runner.base.utils.MLog;

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = BootReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if ((boolean) UserPreferences.getInstance().get("launch_on_device_boot")) {
            MLog.d(TAG, "launching PHONK on boot");
            Intent in = new Intent(context, LauncherActivity.class);
            in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(in);
        }

        String scriptToLaunch = (String) UserPreferences.getInstance().get("launch_script_on_boot");

        if (!scriptToLaunch.isEmpty()) {
            // PhonkAppHelper.launchScript(context, new Project(scriptToLaunch));

            Project p = new Project(scriptToLaunch);
            Intent newIntent = new Intent(context, AppRunnerActivity.class);
            newIntent.putExtra("projectName", p.getName());
            newIntent.putExtra("projectFolder", p.getFolder());
            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(newIntent);
        }
    }
}
