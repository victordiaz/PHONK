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

package io.phonk.runner;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.Map;

import io.phonk.runner.apprunner.AppRunnerHelper;
import io.phonk.runner.base.utils.AndroidUtils;
import io.phonk.runner.base.utils.MLog;
import io.phonk.runner.models.Project;

public class AppRunnerLauncher extends Service {

    private static final String TAG = AppRunnerLauncher.class.getSimpleName();
    private static boolean multiWindowEnabled = true;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Map<String, Object> map = AppRunnerHelper.readProjectProperties(this, p);
        boolean isService = (boolean) map.get("background_service");

        if (isService) {
            Intent intent = new Intent(this, AppRunnerService.class);
            intent.putExtra(Project.FOLDER, p.getFolder());
            intent.putExtra(Project.NAME, p.getName());
            intent.putExtra(Project.SERVER_PORT, PhonkSettings.HTTP_PORT);
            intent.putExtra("device_id", (String) UserPreferences.getInstance().get("device_id"));
            this.startService(intent);
        } else {
            Intent intent = new Intent(this, AppRunnerActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Project.FOLDER, p.getFolder());
            intent.putExtra(Project.NAME, p.getName());
            intent.putExtra(Project.SERVER_PORT, PhonkSettings.HTTP_PORT);
            intent.putExtra("device_id", (String) UserPreferences.getInstance().get("device_id"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            MLog.d(TAG, "1 ------------> launching side by side " + AndroidUtils.isVersionN());

            if (AndroidUtils.isVersionN() && multiWindowEnabled) {
                MLog.d(TAG, "2 ------------> launching side by side");
                // intent.addFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT);
            }
            this.startActivity(intent);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
