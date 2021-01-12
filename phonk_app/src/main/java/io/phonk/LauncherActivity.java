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
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import io.phonk.gui.UpdateActivity;
import io.phonk.gui.WelcomeActivity;
import io.phonk.gui.settings.SettingsActivity;

public class LauncherActivity extends Activity {

    Intent intent = null;
    private String TAG = LauncherActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // this Activity chooses between launching the welcome installer
        // or the app it self
        SharedPreferences userDetails = getSharedPreferences("io.phonk", MODE_PRIVATE);
        boolean firstLaunch = userDetails.getBoolean(getResources().getString(R.string.pref_is_first_launch), true);
        // firstLaunch = true;

        // uncomment to reset (true) first launch
        // userDetails.edit().putBoolean(getResources().getString(R.string.pref_is_first_launch), true).commit();
        // userDetails.edit().putInt("last_version_reinstalled", 126).commit();

        Intent i = getIntent();
        boolean wasCrash = i.getBooleanExtra("wasCrash", false);
        if (wasCrash) {
            Toast.makeText(this, "The script crashed :(", Toast.LENGTH_LONG).show();
        }

        /*
         * Launch WelcomeActivity if first time
         * Launch Update if there is a higher version code
         * or Launch the MainApp
         */
        if (firstLaunch) {
            intent = new Intent(this, WelcomeActivity.class);
        } else if (BuildConfig.VERSION_CODE > userDetails.getInt("last_version_reinstalled", 125)) {
            // Toast.makeText(this, "this needs to update", Toast.LENGTH_LONG).show();
            intent = new Intent(this, UpdateActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
            // intent = new Intent(this, NewMainActivity.class);
            // intent = new Intent(this, SettingsActivity.class);
            // intent.putExtras();
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
