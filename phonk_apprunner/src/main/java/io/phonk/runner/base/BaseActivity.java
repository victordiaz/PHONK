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

package io.phonk.runner.base;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

import io.phonk.runner.R;
import io.phonk.runner.apprunner.AppRunnerSettings;
import io.phonk.runner.base.media.Audio;
import io.phonk.runner.base.utils.AndroidUtils;
import io.phonk.runner.base.utils.MLog;

@SuppressLint("NewApi")
public class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();

    protected Toolbar mToolbar;
    public boolean isToolbarAllowed = true;
    private boolean lightsOutMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // System.gc();
    }

    @Override
    protected void onResume() {
        // System.gc();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // System.gc();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    // Create the action bar programmatically
    protected void setupActivity() {
        if (!AndroidUtils.isWear(this)) {
            mToolbar = findViewById(R.id.toolbar2);
            setSupportActionBar(mToolbar);
            // getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    protected void enableBackOnToolbar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void setFullScreen() {
        isToolbarAllowed = true;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void setImmersive() {
        isToolbarAllowed = false;
        // getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }

    public int getStatusBarSize() {
        Rect rectangle = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        int statusBarHeight = rectangle.top;
        int contentViewTop = window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
        int titleBarHeight = contentViewTop - statusBarHeight;
        return titleBarHeight;
    }

    public int getNavigationBarSize() {
        Resources resources = getApplicationContext().getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public void setNormal() {
        getSupportActionBar().show();
        isToolbarAllowed = true;

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void showHomeBar(boolean b) {

        if (Build.VERSION.SDK_INT > AppRunnerSettings.MIN_SUPPORTED_VERSION) {
            if (b == true) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            } else {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            }
        }
    }

    public void lightsOutMode() {
        lightsOutMode = true;
        final View rootView = getWindow().getDecorView();
        rootView.setSystemUiVisibility(View.STATUS_BAR_VISIBLE);
        rootView.setSystemUiVisibility(View.STATUS_BAR_HIDDEN);

        rootView.setOnSystemUiVisibilityChangeListener(visibility -> {
            MLog.d(TAG, "" + visibility);
            rootView.setSystemUiVisibility(View.STATUS_BAR_VISIBLE);
            rootView.setSystemUiVisibility(View.STATUS_BAR_HIDDEN);
        });
    }

    public void setScreenAlwaysOn(boolean b) {
        if (b) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }


    public void changeFragment(int id, Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(id, fragment);
        fragmentTransaction.commit();
    }


    public void addFragment(Fragment f, int id, String tag) {
        FrameLayout fl = findViewById(id);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(fl.getId(), f, tag);
        ft.commit();
    }

    public void addFragment(Fragment fragment, int fragmentPosition, String tag, boolean addToBackStack) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        ft.add(fragmentPosition, fragment, tag);
        // ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        if (addToBackStack) {
            ft.addToBackStack(null);
        }
        ft.commit();
    }


    public void addFragment(Fragment fragment, int fragmentPosition, boolean addToBackStack) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // FIXME: Because we have no tagging system we need to use the int as mContext
        // tag, which may cause collisions
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        ft.add(fragmentPosition, fragment, String.valueOf(fragmentPosition));
        // ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        if (addToBackStack) {
            ft.addToBackStack(null);
        }
        ft.commit();
    }

    public void removeFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        ft.remove(fragment);
        ft.commit();
    }

    public void setBrightness(float f) {
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        layoutParams.screenBrightness = f;
        getWindow().setAttributes(layoutParams);
    }

    public float getCurrentBrightness() {
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();

        return layoutParams.screenBrightness;
    }

    // override home buttons
    @Override
    public void onAttachedToWindow() {
        //this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
        super.onAttachedToWindow();
    }

    /**
     * Handle the results from the recognition activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Audio.VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            // Fill the list view with the strings the recognizer thought it
            // could have heard
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            for (String _string : matches) {
                MLog.d(TAG, "" + _string);
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    // override volume buttons
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        MLog.d(TAG, "" + keyCode);

        //TODO reenable this
//        if (AppRunnerSettings.OVERRIDE_VOLUME_BUTTONS
//                && (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP)) {
//            return true;
//        }
//
//        if (keyCode == KeyEvent.KEYCODE_BACK && AppRunnerSettings.CLOSE_WITH_BACK) {
//            finish();
//            return true;
//        }

        return super.onKeyDown(keyCode, event);
    }

    public void superMegaForceKill() {
        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);
    }

}
