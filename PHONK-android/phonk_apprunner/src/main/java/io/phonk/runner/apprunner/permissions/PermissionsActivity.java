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

package io.phonk.runner.apprunner.permissions;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.phonk.runner.base.BaseActivity;
import io.phonk.runner.base.utils.MLog;

public class PermissionsActivity extends BaseActivity {

    private static final String TAG = PermissionsActivity.class.getSimpleName();
    private static final int REQUEST_CODE_SOME_FEATURES_PERMISSIONS = 11;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setupActivity() {
        super.setupActivity();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermission(boolean request, String... p) {
        List<String> requiredPermissions = new ArrayList<>();

        requiredPermissions.addAll(Arrays.asList(p));

        // check if permission is already granted
        for (int i = 0; i < requiredPermissions.size(); i++) {
            String permissionName = requiredPermissions.get(i);
            int isGranted1 = checkSelfPermission(permissionName);
            int isGranted2 = isGranted1 & PackageManager.PERMISSION_GRANTED;

            MLog.d(TAG, permissionName + " " + isGranted1 + " " + isGranted2);
        }

        // request the permissions
        if (request && !requiredPermissions.isEmpty()) {
            requestPermissions(requiredPermissions.toArray(new String[requiredPermissions.size()]), REQUEST_CODE_SOME_FEATURES_PERMISSIONS);
        }
    }

    private void checkAllPermissions(boolean request) {
        List<String> requiredPermissions = new ArrayList<>();

        requiredPermissions.add(Manifest.permission.INTERNET);
        requiredPermissions.add(Manifest.permission.ACCESS_WIFI_STATE);
        requiredPermissions.add(Manifest.permission.CHANGE_WIFI_STATE);
        requiredPermissions.add(Manifest.permission.CHANGE_WIFI_MULTICAST_STATE);
        requiredPermissions.add(Manifest.permission.ACCESS_NETWORK_STATE);
        requiredPermissions.add(Manifest.permission.VIBRATE);
        requiredPermissions.add(Manifest.permission.WAKE_LOCK);
        requiredPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        requiredPermissions.add(Manifest.permission.READ_PHONE_STATE);
        requiredPermissions.add(Manifest.permission.BLUETOOTH);
        requiredPermissions.add(Manifest.permission.BLUETOOTH_ADMIN);
        requiredPermissions.add(Manifest.permission.WRITE_SETTINGS);
        requiredPermissions.add(Manifest.permission.NFC);
        requiredPermissions.add(Manifest.permission.RECEIVE_SMS);
        requiredPermissions.add(Manifest.permission.SEND_SMS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            requiredPermissions.add(Manifest.permission.INSTALL_SHORTCUT);
        }
        requiredPermissions.add(Manifest.permission.CAMERA);
        // requiredPermissions.add(Manifest.permission.FLASHLIGHT);
        requiredPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        requiredPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        requiredPermissions.add(Manifest.permission.MODIFY_AUDIO_SETTINGS);
        requiredPermissions.add(Manifest.permission.RECORD_AUDIO);
        requiredPermissions.add(Manifest.permission.SYSTEM_ALERT_WINDOW);

        // check if permission is already granted
        for (int i = 0; i < requiredPermissions.size(); i++) {
            String permissionName = requiredPermissions.get(i);
            int isGranted1 = checkSelfPermission(permissionName);
            int isGranted2 = isGranted1 & PackageManager.PERMISSION_GRANTED;

            MLog.d(TAG, permissionName + " " + isGranted1 + " " + isGranted2);
        }

        // request the permissions
        if (!requiredPermissions.isEmpty()) {
            requestPermissions(requiredPermissions.toArray(new String[requiredPermissions.size()]), REQUEST_CODE_SOME_FEATURES_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE_SOME_FEATURES_PERMISSIONS) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    MLog.d("Permissions", "Permission Granted: " + permissions[i]);
                } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    MLog.d("Permissions", "Permission Denied: " + permissions[i]);
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
