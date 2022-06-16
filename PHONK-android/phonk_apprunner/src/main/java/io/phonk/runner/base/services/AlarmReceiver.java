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

package io.phonk.runner.base.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import io.phonk.runner.AppRunnerActivity;
import io.phonk.runner.base.utils.MLog;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = AlarmReceiver.class.getSimpleName();

    private void startComponent(Context c, Intent intent) {
        Intent newIntent = new Intent(c, AppRunnerActivity.class);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        newIntent.putExtras(intent);
        c.startActivity(newIntent);
        MLog.d(TAG, "ww starting activity");
    }

    @Override
    public void onReceive(Context c, Intent intent) {
        MLog.d(TAG, "ww alarm started");

        try {
            startComponent(c, intent);
        } catch (Exception e) {
            MLog.d(TAG, "ww error on the alarm");
            Toast.makeText(c, "There was an error somewhere, but we still received an alarm", Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();

        }
    }
}