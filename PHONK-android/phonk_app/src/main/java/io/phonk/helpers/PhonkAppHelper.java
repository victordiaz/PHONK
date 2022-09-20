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

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.phonk.MainActivity;
import io.phonk.events.Events;
import io.phonk.gui.AboutActivity;
import io.phonk.gui.HelpActivity;
import io.phonk.gui.InfoScriptActivity;
import io.phonk.gui.LicenseActivity;
import io.phonk.gui.SchedulerActivity;
import io.phonk.gui._components.NewProjectDialogFragment;
import io.phonk.gui.editor.EditorActivity;
import io.phonk.gui.settings.PhonkSettings;
import io.phonk.gui.settings.SettingsActivity;
import io.phonk.gui.settings.UserPreferences;
import io.phonk.runner.AppRunnerLauncherService;
import io.phonk.runner.apprunner.AppRunnerSettings;
import io.phonk.runner.base.models.Project;
import io.phonk.runner.base.utils.MLog;

public class PhonkAppHelper {
    private static final String TAG = PhonkAppHelper.class.getSimpleName();

    public static void launchScript(Context context, Project p, HashMap<String, String> extras) {
        Intent intent = new Intent(context, AppRunnerLauncherService.class);
        intent.putExtra(Project.SERVER_PORT, PhonkSettings.HTTP_PORT);
        intent.putExtra(Project.FOLDER, p.getFolder());
        intent.putExtra(Project.NAME, p.getName());
        intent.putExtra(Project.DEVICE_ID, (String) UserPreferences.getInstance().get("device_id"));
        intent.putExtra(Project.SETTINGS_SCREEN_WAKEUP, (Boolean) UserPreferences.getInstance().get("device_wakeup_on_play"));

        for (Map.Entry<String, String> entry : extras.entrySet()) {
            intent.putExtra(entry.getKey(), entry.getValue());
        }

        EventBus.getDefault().post(new Events.ProjectEvent(Events.PROJECT_RUNNING, p));
        context.startService(intent);
    }

    public static void launchScript(Context context, Project p) {
        PhonkAppHelper.launchScript(context, p, new HashMap<>());
    }

    public static void launchSettings(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }

    public static void launchEditor(Context context, Project p) {
        MLog.d(TAG, "starting editor");
        Intent intent = new Intent(context, EditorActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Project.FOLDER, p.getFolder());
        intent.putExtra(Project.NAME, p.getName());

        context.startActivity(intent);
    }

    public static void launchLicense(Context context) {
        Intent intent = new Intent(context, LicenseActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void launchSchedulerList(Context context) {
        Intent intent = new Intent(context, SchedulerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void launchScriptInfoActivity(Context context, Project p) {
        Intent intent = new Intent(context, InfoScriptActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Project.FOLDER, p.getFolder());
        intent.putExtra(Project.NAME, p.getName());

        context.startActivity(intent);
    }

    public static void launchHelp(Context context) {
        Intent intent = new Intent(context, HelpActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void launchAbout(Context context) {
        Intent intent = new Intent(context, AboutActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void launchWifiSettings(Context context) {
        // context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
        Intent intent = new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (intent.resolveActivityInfo(context.getPackageManager(), 0) != null) {
            context.startActivity(intent);
        }
    }

    public static void launchHotspotSettings(Context context) {
        // context.startActivity(new Intent(WifiManager.AC));

        final Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        final ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.TetherSettings");
        intent.setComponent(cn);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (intent.resolveActivityInfo(context.getPackageManager(), 0) != null) {
            context.startActivity(intent);
        }
    }

    public static void launchAppSettings(Context c) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", c.getPackageName(), null);
        intent.setData(uri);
        // c.startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
        c.startActivity(intent);
    }

    public static void newProjectDialog(final MainActivity c) {
        FragmentManager fm = c.getSupportFragmentManager();
        NewProjectDialogFragment newProjectDialog = new NewProjectDialogFragment();
        newProjectDialog.show(fm, "fragment_edit_name");

        String[] templates = PhonkScriptHelper.listTemplates(c);
        for (String template : templates) {
            MLog.d(TAG, "template " + template);
        }

        newProjectDialog.setListener(inputText -> {
            if (inputText.isEmpty()) {
                Toast.makeText(c, "Project cannot be created " + inputText, Toast.LENGTH_SHORT).show();
            } else {
                String template = "default";
                Toast.makeText(c, "Creating " + inputText, Toast.LENGTH_SHORT).show();
                Project p = PhonkScriptHelper.createNewProject(c, template, AppRunnerSettings.USER_PROJECTS_FOLDER + "/User Projects/", inputText);
                EventBus.getDefault().post(new Events.ProjectEvent(Events.PROJECT_NEW, p));
            }
        });
    }

    public static void openInWebEditor(final Context c, Project p) {
        Intent i = new Intent("io.phonk.intent.WEBEDITOR_SEND");

        String[] splitted = p.getFolder().split("/"); // separating type and folder
        i.putExtra("action", "load_project");
        i.putExtra("type", splitted[0]);
        i.putExtra("folder", splitted[1]);
        i.putExtra("name", p.getName());
        c.sendBroadcast(i);
    }

    public static String readFile(Context c, String path) {
        InputStream is = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try {
            is = c.getAssets().open(path);

            int i;
            try {
                i = is.read();
                while (i != -1) {
                    byteArrayOutputStream.write(i);
                    i = is.read();
                }
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return byteArrayOutputStream.toString();
    }

    public static void requestPermissions(Activity ac, int requestCode) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
            Toast.makeText(ac, "Permissions are only available from Android 6.0", Toast.LENGTH_LONG).show();
            return;
        }

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
        requiredPermissions.add(Manifest.permission.INSTALL_SHORTCUT);
        requiredPermissions.add(Manifest.permission.CAMERA);

        // requiredPermissions.add(Manifest.permission.FLASHLIGHT);

        requiredPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        requiredPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        requiredPermissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        requiredPermissions.add(Manifest.permission.MODIFY_AUDIO_SETTINGS);
        requiredPermissions.add(Manifest.permission.RECORD_AUDIO);
        requiredPermissions.add(Manifest.permission.SYSTEM_ALERT_WINDOW);

        // check if permission is already granted
        for (int i = 0; i < requiredPermissions.size(); i++) {
            String permissionName = requiredPermissions.get(i);
            int isGranted1 = ac.checkSelfPermission(permissionName);
            int isGranted2 = isGranted1 & PackageManager.PERMISSION_GRANTED;

            MLog.d(TAG, permissionName + " " + isGranted1 + " " + isGranted2);
            // requiredPermissions.remove(i);
        }

        MLog.d(TAG, "" + requiredPermissions.isEmpty());

        // request the permissions
        //if (!requiredPermissions.isEmpty()) {
        ac.requestPermissions(requiredPermissions.toArray(new String[requiredPermissions.size()]), requestCode);
        //}
    }

}
