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

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;

import org.phonk.R;

import io.phonk.gui.settings.UserPreferences;
import io.phonk.gui.settings.PhonkSettings;
import io.phonk.helpers.PhonkSettingsHelper;
import io.phonk.runner.base.BaseActivity;
import io.phonk.runner.base.utils.AndroidUtils;
import io.phonk.runner.base.utils.MLog;
import io.phonk.runner.base.utils.StrUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("NewApi")
public class WelcomeActivity extends BaseActivity {

    private static final String TAG = WelcomeActivity.class.getSimpleName();
    private static final int REQUEST_CODE_SOME_FEATURES_PERMISSIONS = 11;

    private static final int STEP_WELCOME = 0;
    private static final int STEP_ASK_PERMISSIONS_PROMPT = 1;
    private static final int STEP_ASK_PERMISSIONS_PROCESS = 2;
    private static final int STEP_ASK_PERMISSIONS_ERROR = 3;
    private static final int STEP_ASK_PERMISSIONS_OK = 4;
    private static final int STEP_INSTALL_EXAMPLES_PROMPT = 5;
    private static final int STEP_INSTALL_EXAMPLES_PROCESS = 6;
    private static final int STEP_INSTALL_EXAMPLES_OK = 7;
    private static final int STEP_READY = 8;

    private Button mNextStepButton;
    private ViewFlipper viewFlipper;
    private int mCurrentStep = 0;
    private int mNextStep = 0;
    private LinearLayout mLoading;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.welcome_activity);
        setupActivity();

        viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        viewFlipper.setInAnimation(this, R.anim.slide_in_left);
        viewFlipper.setOutAnimation(this, R.anim.slide_out_right);

        mNextStepButton = (Button) findViewById(R.id.next_step_button);
        mNextStepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToStep(mNextStep);
            }
        });
        mLoading = (LinearLayout) findViewById(R.id.loading);

        goToStep(STEP_WELCOME);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            mNextStepButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.splash_slide_in_anim_set));
        }
    }

    @Override
    protected void setupActivity() {
        super.setupActivity();
        mToolbar.setVisibility(View.GONE);
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

    private void checkPermissions() {
        List<String> requiredPermissions = new ArrayList<String>();

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
        requiredPermissions.add(Manifest.permission.INSTALL_SHORTCUT);
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
        boolean isPermissionWriteExternalGranted = false;

        switch (requestCode) {
            case REQUEST_CODE_SOME_FEATURES_PERMISSIONS: {
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        MLog.d("Permissions", "Permission Granted: " + permissions[i]);
                    } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        MLog.d("Permissions", "Permission Denied: " + permissions[i]);
                    }

                    if (permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        isPermissionWriteExternalGranted = true;
                        MLog.d(TAG, "granted!");
                    }
                    // if is already granted we remove it from the list
                    // requiredPermissions.remove(i);
                }

                // if ok go next step otherwise repeat step
                if (isPermissionWriteExternalGranted) goToStep(STEP_ASK_PERMISSIONS_OK);
                else goToStep(STEP_ASK_PERMISSIONS_ERROR);
            }
            break;
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    public void installExamples() {
        //create folder structure
        new File(PhonkSettings.getFolderPath(PhonkSettings.USER_PROJECTS_FOLDER)).mkdirs();
        new File(PhonkSettings.getFolderPath(PhonkSettings.EXAMPLES_FOLDER)).mkdirs();
        new File(PhonkSettings.getBaseWebEditorsDir()).mkdirs();
        new File(PhonkSettings.getBaseLibrariesDir()).mkdirs();

        // install examples
        PhonkSettingsHelper.installExamples(getApplicationContext(), PhonkSettings.EXAMPLES_FOLDER, new PhonkSettingsHelper.InstallListener() {
            @Override
            public void onReady() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        goToStep(STEP_INSTALL_EXAMPLES_OK);
                    }
                });
            }
        });

    }

    public void ready() {
        // Start the activity
        Intent i = new Intent(WelcomeActivity.this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }

    public void goToStep(int step) {
        MLog.d(TAG, "step " + step + " " + mNextStep);

        mCurrentStep = step;

        switch (step) {
            case STEP_WELCOME:
                if (AndroidUtils.isVersionMarshmallow()) {
                    mNextStep = STEP_ASK_PERMISSIONS_PROMPT;
                } else {
                    mNextStep = STEP_INSTALL_EXAMPLES_PROMPT;
                }

                break;

            case STEP_ASK_PERMISSIONS_PROMPT:
                viewFlipper.setDisplayedChild(1);
                mNextStepButton.setText("ask permissions");
                mNextStep = STEP_ASK_PERMISSIONS_PROCESS;

                break;

            case STEP_ASK_PERMISSIONS_PROCESS:
                mNextStepButton.setEnabled(false);

                checkPermissions();

                // show some feedback

                break;

            case STEP_ASK_PERMISSIONS_ERROR:
                mNextStepButton.setEnabled(true);
                mNextStepButton.setText("ask permissions");
                mNextStep = STEP_ASK_PERMISSIONS_PROMPT;

                // hide feedback process

                // show feedback error

                break;

            case STEP_ASK_PERMISSIONS_OK:
                // hide feedback process
                // show feedback OK

                // after some time go to next
                goToStep(STEP_INSTALL_EXAMPLES_PROMPT);

                break;

            case STEP_INSTALL_EXAMPLES_PROMPT:
                viewFlipper.setDisplayedChild(2);
                mNextStepButton.setEnabled(true);
                mNextStepButton.setText("Install examples");
                mNextStep = STEP_INSTALL_EXAMPLES_PROCESS;

                break;

            case STEP_INSTALL_EXAMPLES_PROCESS:
                installExamples();

                // show feedback process
                mNextStepButton.setVisibility(View.GONE);
                mLoading.setVisibility(View.VISIBLE);

                break;

            case STEP_INSTALL_EXAMPLES_OK:
                // viewFlipper.setDisplayedChild(3);
                mNextStep = STEP_READY;

                // disable feedback process
                mNextStepButton.setVisibility(View.VISIBLE);
                mLoading.setVisibility(View.GONE);

                mNextStepButton.setText("START CREATING");

                break;

            case STEP_READY:
                // Write a shared pref to never come back here
                SharedPreferences userDetails = getSharedPreferences("org.protocoder", MODE_PRIVATE);
                userDetails.edit().putBoolean(getResources().getString(R.string.pref_is_first_launch), false).commit();

                // first time id
                UserPreferences.getInstance().set("user_id", StrUtils.generateUUID()).save();

                ready();

                break;

        }
        step++;

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
