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

package io.phonk.helpers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;

import io.phonk.MainActivity;
import io.phonk.gui.settings.PhonkSettings;
import io.phonk.runner.base.utils.FileIO;

public class PhonkSettingsHelper {

    private static final String PHONK_EXTENSION = ".phonk";

    public static void showRestartMessage(final Context mContext, View view) {

        Snackbar.make(view, "Close the app to see the changes", Snackbar.LENGTH_LONG)
                .setAction("RESTART", view1 -> {

                    Intent mStartActivity = new Intent(mContext, MainActivity.class);
                    int mPendingIntentId = 123456;
                    PendingIntent mPendingIntent = PendingIntent.getActivity(mContext, mPendingIntentId, mStartActivity,
                            PendingIntent.FLAG_CANCEL_CURRENT);
                    AlarmManager mgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                    mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 250, mPendingIntent);
                    System.exit(0);
                }).show();
    }

    public interface InstallListener {
        void onReady();
    }

    public static void installExamples(final Context c, final InstallListener l) {
        new Thread(() -> {
            File examplesFolder = new File(PhonkSettings.getBaseDir() + PhonkSettings.EXAMPLES_FOLDER);
            FileIO.deleteDir(examplesFolder);
            FileIO.copyAssetFolder(c.getAssets(), PhonkSettings.EXAMPLES_FOLDER, examplesFolder.getAbsolutePath());

            File demosFolder = new File(PhonkSettings.getBaseDir() + PhonkSettings.DEMOS_FOLDER);
            FileIO.deleteDir(demosFolder);
            // FileIO.copyAssetFolder(c.getAssets(), "contributed_projects", demosFolder.getAbsolutePath());

            l.onReady();
        }).start();
    }

}
