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
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.phonk.events.Events;
import io.phonk.gui.connectionInfo.ConnectionInfoFragment;
import io.phonk.gui.projectbrowser.ProjectBrowserFragment;
import io.phonk.gui._components.APIWebviewFragment;
import io.phonk.gui._components.NewProjectDialogFragment;
import io.phonk.gui.projectbrowser.projectlist.ProjectItem;
import io.phonk.gui.projectbrowser.projectlist.ProjectListFragment;
import io.phonk.gui.settings.PhonkSettings;
import io.phonk.gui.settings.UserPreferences;
import io.phonk.helpers.PhonkAppHelper;
import io.phonk.helpers.PhonkScriptHelper;
import io.phonk.runner.apprunner.AppRunnerHelper;
import io.phonk.runner.base.BaseActivity;
import io.phonk.runner.base.models.Project;
import io.phonk.runner.base.network.NetworkUtils;
import io.phonk.runner.base.utils.AndroidUtils;
import io.phonk.runner.base.utils.MLog;
import io.phonk.server.PhonkServerService;
import io.phonk.server.model.ProtoFile;

public class MainActivity extends BaseActivity {

    private static final java.lang.String TAG = MainActivity.class.getSimpleName();

    private Intent mServerIntent;

    private ProjectBrowserFragment mProjectBrowserFragment;

    private ImageButton mToggleConnectionInfo;
    private CardView mConnectionInfo;

    private ConnectionInfoFragment mConnectionInfoFragment;
    private APIWebviewFragment mWebViewFragment;

    private boolean mIsWebIdeMode = false;
    private boolean mAlreadyStartedServices = false;
    private boolean mIsConfigChanging = false;

    private void listProjectsWithControls() {
        ArrayList<ProtoFile> userFolder = PhonkScriptHelper.listProjectsInFolder(PhonkSettings.USER_PROJECTS_FOLDER, 2);
        ArrayList<ProtoFile> examplesFolder = PhonkScriptHelper.listProjectsInFolder(PhonkSettings.EXAMPLES_FOLDER, 2);

        for (ProtoFile protoFile : examplesFolder) {
            for (ProtoFile projectFolder : protoFile.files) {
                Project p = new Project(projectFolder.getPath());
                Map<String, Object> scriptSettings = AppRunnerHelper.readProjectProperties(getApplicationContext(), p);
                boolean isDeviceControl = (boolean) scriptSettings.get("device_control");

                if (isDeviceControl) {
                    MLog.d(TAG,  "yep: " + p.getFullPath() + " " + isDeviceControl + " : " + p.getFullPath());
                } else {
                    // MLog.d(TAG, "nop: " + p.getFullPath() + " " + isDeviceControl + " : " + p.getFullPath());
                }
            }
        }
    }

    private void listProjectsWithControlsThread() {
        Thread t = new Thread(() -> {
            try {
                listProjectsWithControls();
            } catch (Exception e) {
                MLog.d(TAG, "Error:" + e);
            }
        });
        t.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // listProjectsWithControlsThread();

        // PhonkAppHelper.launchSchedulerList(this);
        EventBus.getDefault().register(this);

        UserPreferences.getInstance().load();

        // UserPreferences.getInstance().set("webide_mode", false).save();
        mIsWebIdeMode = (boolean) UserPreferences.getInstance().get("webide_mode");

        if (savedInstanceState != null) {
            mAlreadyStartedServices = savedInstanceState.getBoolean("alreadyStartedServices", false);
        }

        // startServers if conf specifies. In webidemode always have to start it
        MLog.d(TAG, "isWebIdeMode " + mIsWebIdeMode);
        if (mIsWebIdeMode) startServers();

        loadUI();
        setScreenAlwaysOn((boolean) UserPreferences.getInstance().get("screen_always_on"));

        // execute onLaunch script
        String script = (String) UserPreferences.getInstance().get("launch_script_on_app_launch");
        if (!script.isEmpty()) {
            Project p = new Project(script);
            PhonkAppHelper.launchScript(this, p);
        }

        // PhonkAppHelper.launchScript(this, new Project("playground/User Projects/activity"), extras);
        // PhonkAppHelper.launchScript(this, new Project("examples/Graphical User Interface/Basic Views"));
        // PhonkAppHelper.launchScript(this, new Project("examples/Graphical User Interface/Styling & Theming"));
        // PhonkAppHelper.launchScript(this, new Project("examples/Graphical User Interface/Extra Views"));
    }

    @Override
    protected void onResume() {
        super.onResume();

        startBroadCastReceiver();

        // send appIsClosed
        Intent i = new Intent("io.phonk.intent.CLOSED");
        sendBroadcast(i);
        registerReceiver(connectivityChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(adbBroadcastReceiver);
        unregisterReceiver(connectivityChangeReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (!mIsConfigChanging) stopServers();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("alreadyStartedServices", true);
        mIsConfigChanging = true;
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void loadUI() {
        // load UI
        setContentView(R.layout.main_activity);

        mProjectBrowserFragment = ProjectBrowserFragment.newInstance(ProjectItem.MODE_NORMAL);
        mProjectBrowserFragment.setProjectClickListener(new ProjectListFragment.ProjectSelectedListener() {
            @Override
            public void onProjectSelected(Project p) {
                MLog.d("launching", p.getSandboxPath());
                PhonkAppHelper.launchScript(MainActivity.this, p);
            }

            @Override
            public void onMultipleProjectsSelected(HashMap<Project, Boolean> projects) {
            }

            @Override
            public void onActionClicked(String action) {

            }
        });

        // Create Fragments
        addFragment(mProjectBrowserFragment, R.id.projectBrowserFragment, false);

        // Connection Fragment
        mConnectionInfoFragment = ConnectionInfoFragment.newInstance();
        addFragment(mConnectionInfoFragment, R.id.infoLayout, false);

        mConnectionInfo = findViewById(R.id.ip_container);
        mToggleConnectionInfo = findViewById(R.id.toggleConnectionInfo);

        final ImageButton moreOptionsButton = findViewById(R.id.more_options);
        moreOptionsButton.setOnClickListener(view -> {
            Context wrapper = new ContextThemeWrapper(MainActivity.this, R.style.phonk_PopupMenu);
            PopupMenu myPopup = new PopupMenu(wrapper, moreOptionsButton);
            myPopup.inflate(R.menu.more_options);
            myPopup.setOnMenuItemClickListener(menuItem -> {
                int itemId = menuItem.getItemId();

                if (itemId == R.id.more_options_new) {
                    PhonkAppHelper.newProjectDialog(MainActivity.this);
                    return true;
                } else if (itemId == R.id.more_options_settings) {
                    PhonkAppHelper.launchSettings(MainActivity.this);
                    return true;
                } else if (itemId == R.id.more_options_help) {
                    PhonkAppHelper.launchHelp(MainActivity.this);
                    return true;
                } else if (itemId == R.id.more_options_about) {
                    PhonkAppHelper.launchAbout(MainActivity.this);
                    return true;
                }

                return false;
            });

            myPopup.show();
        });

        // sendDelayedEvent("version", BuildConfig.VERSION_NAME, 500);
        String[] versionNameTemp = BuildConfig.VERSION_NAME.split("_");
        String friendlyVersionName = "(unknown version)";
        if (versionNameTemp.length > 1) {
            friendlyVersionName = versionNameTemp[0];
            if (!versionNameTemp[1].equals("normal")) friendlyVersionName += " (" + versionNameTemp[1] + ").";
        }
        sendDelayedEvent("welcome", "Welcome to PHONK " + friendlyVersionName + " Enjoy!", 1000);
    }

    public void sendDelayedEvent(String type, String message, int delay) {
        Handler handler = new Handler(getMainLooper());
        handler.postDelayed(() -> EventBus.getDefault().post(new Events.AppUiEvent(type, message)), delay);
    }

    public void loadWebIde() {
        MLog.d(TAG, "loadWebIde");

        if (mWebViewFragment != null) return;

        FrameLayout fl = findViewById(R.id.webEditorFragment);
        fl.setVisibility(View.VISIBLE);
        mWebViewFragment = new APIWebviewFragment();

        Bundle bundle = new Bundle();
        String url = "http://127.0.0.1:8585";
        // String url = "http://10.0.2.2:8080";
        bundle.putString("url", url);
        bundle.putBoolean("isTablet", true);
        mWebViewFragment.setArguments(bundle);

        addFragment(mWebViewFragment, R.id.webEditorFragment, "WebIDE");
    }

    public void createProjectDialog() {
        FragmentManager fm = getSupportFragmentManager();
        NewProjectDialogFragment newProjectDialog = new NewProjectDialogFragment();
        newProjectDialog.show(fm, "fragment_edit_name");

        String[] templates = PhonkScriptHelper.listTemplates(this);
        for (String template : templates) {
            MLog.d(TAG, "template " + template);
        }

        newProjectDialog.setListener(inputText -> {
            String template = "default";
            Toast.makeText(MainActivity.this, "Creating " + inputText, Toast.LENGTH_SHORT).show();
            Project p = PhonkScriptHelper.createNewProject(MainActivity.this, template, "user_projects/User Projects/", inputText);
            EventBus.getDefault().post(new Events.ProjectEvent(Events.PROJECT_NEW, p));
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) return true;
        return super.onOptionsItemSelected(item);
    }

    /*
     * This broadcast will receive JS commands if is in debug mode, useful to debug the app through adb
     */
    BroadcastReceiver adbBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String cmd = intent.getStringExtra("cmd");
            MLog.d(TAG, "executing >> " + cmd);
            // mAppRunner.interp.eval(cmd);
        }
    };

    private void startBroadCastReceiver() {
        if (PhonkSettings.DEBUG) {
            //execute commands from intents
            //ie: adb shell am broadcast -a io.phonk.intent.EXECUTE --es cmd "device.vibrate(100)"

            IntentFilter filterSend = new IntentFilter();
            filterSend.addAction("io.phonk.intent.EXECUTE");
            registerReceiver(adbBroadcastReceiver, filterSend);
        }
    }

    /*
     * Server
     */
    private void startServers() {
        MLog.d(TAG, "starting servers");
        mServerIntent = new Intent(this, PhonkServerService.class);
        //serverIntent.putExtra(Project.FOLDER, folder);
        startService(mServerIntent);
    }

    private void stopServers() {
        if (mServerIntent != null) stopService(mServerIntent);
    }

    // execute lines
    @Subscribe
    public void onEventMainThread(Events.ExecuteCodeEvent e) {
        String code = e.getCode();
        MLog.d(TAG, "connect -> " + code);

        if (PhonkSettings.DEBUG) {
            // mAppRunner.interp.eval(code);
        }
    }

    @Subscribe
    public void onEventMainThread(Events.ProjectEvent e) {
        if (e.getAction().equals(Events.CLOSE_APP)) {
            MLog.d(TAG, "closing app (not implemented)");
        }
    }

    // folder choose
    @Subscribe
    public void onEventMainThread(Events.FolderChosen e) {
        MLog.d(TAG, "< Event (folderChosen)");
        // mViewPager.setCurrentItem(2, true);
    }

    @Subscribe
    public void onEventMainThread(Events.AppUiEvent e) {
        String action = e.getAction();
        Object value = e.getValue();
        MLog.d(TAG, "got AppUiEvent " + action);

        switch (action) {
            case "page":
                // mViewPager.setCurrentItem((int) value);
                break;
            case "stopServers":
                stopServers();
                break;
            case "startServers":
                if (!mAlreadyStartedServices)
                    startServers();
                break;
            case "serversStarted":
                // show webview
                if (mIsWebIdeMode) loadWebIde();
                break;
            case "recreate":
                break;
        }
    }

    /*
     * Network Connectivity listener
     */
    BroadcastReceiver connectivityChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MLog.d(TAG, "connectivity changed");
            AndroidUtils.debugIntent("connectivityChangerReceiver", intent);

            // check if there is a WIFI connection or we can connect via USB
            if (NetworkUtils.getLocalIpAddress(MainActivity.this).get("ip").equals("127.0.0.1")) {
                MLog.d(TAG, "No WIFI, still you can hack via USB using the adb command");
                EventBus.getDefault().post(new Events.Connection("none", ""));
            } else {
                MLog.d(TAG, "Hack via your browser @ http://" + NetworkUtils.getLocalIpAddress(MainActivity.this) + ":" + PhonkSettings.HTTP_PORT);
                String ip = NetworkUtils.getLocalIpAddress(MainActivity.this).get("ip") + ":" + PhonkSettings.HTTP_PORT;
                String type = (String) NetworkUtils.getLocalIpAddress(MainActivity.this).get("type");
                EventBus.getDefault().post(new Events.Connection(type, ip));
            }
        }
    };

    @Override
    public void onBackPressed() {
        // if we are not in the first page we go dont exit the app
        if (mProjectBrowserFragment.mViewPager.getCurrentItem() != 0) {
            mProjectBrowserFragment.mViewPager.setCurrentItem(0, true);
        } else {
            super.onBackPressed();
        }
    }
}
