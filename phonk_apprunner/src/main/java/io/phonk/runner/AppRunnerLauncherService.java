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

import androidx.annotation.Nullable;

import java.util.Map;

import io.phonk.runner.apprunner.AppRunnerHelper;
import io.phonk.runner.base.models.Project;
import io.phonk.runner.base.utils.AndroidUtils;
import io.phonk.runner.base.utils.MLog;

public class AppRunnerLauncherService extends Service {

    private static final String TAG = AppRunnerLauncherService.class.getSimpleName();
    private static boolean multiWindowEnabled = true;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String projectFolder = intent.getStringExtra("projectFolder");
        String projectName = intent.getStringExtra("projectName");
        int httpPort = intent.getIntExtra("httpPort", -1);
        String deviceId = intent.getStringExtra("projectName");
        boolean wakeUpScreen = intent.getBooleanExtra("settings_wakeUpScreen", false);

        Project p = new Project(projectFolder, projectName);

        Map<String, Object> map = AppRunnerHelper.readProjectProperties(this, p);
        boolean isService = (boolean) map.get("background_service");
        MLog.d(TAG, "launching " + p.getFullPath() + " " + isService);

        Intent newIntent = null;
        if (isService) {
            newIntent = new Intent(this, AppRunnerService.class);
        } else {
            newIntent = new Intent(getApplicationContext(), AppRunnerActivity.class);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            if (AndroidUtils.isVersionN() && multiWindowEnabled)
                intent.addFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT);
        }

        newIntent.putExtras(intent);

        newIntent.putExtra(Project.SERVER_PORT, httpPort);
        newIntent.putExtra(Project.DEVICE_ID, deviceId);
        newIntent.putExtra(Project.SETTINGS_SCREEN_WAKEUP, wakeUpScreen);

        /*
        mBundle.putString(Project.PREFIX, intent.getStringExtra(Project.PREFIX));
        mBundle.putString(Project.INTENTCODE, intent.getStringExtra(Project.INTENTCODE));
        mBundle.putString(Project.POSTFIX, intent.getStringExtra(Project.POSTFIX));
        */

        if (isService) this.startService(newIntent);
        else this.startActivity(newIntent);

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
