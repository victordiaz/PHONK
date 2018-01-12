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

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import io.phonk.MainActivity;
import io.phonk.events.Events;
import io.phonk.gui.HelpActivity;
import io.phonk.gui.InfoScriptActivity;
import io.phonk.gui.LicenseActivity;
import io.phonk.gui.SchedulerActivity;
import io.phonk.gui._components.NewProjectDialogFragment;
import io.phonk.gui.editor.EditorActivity;
import io.phonk.gui.settings.UserPreferences;
import io.phonk.gui.settings.PhonkSettings;
import io.phonk.gui.settings.SettingsActivity;
import io.phonk.runner.AppRunnerActivity;
import io.phonk.runner.AppRunnerService;
import io.phonk.runner.apprunner.AppRunnerHelper;
import io.phonk.runner.base.utils.AndroidUtils;
import io.phonk.runner.base.utils.MLog;
import io.phonk.runner.models.Project;

import java.util.Map;

public class PhonkAppHelper {

    private static final String TAG = PhonkAppHelper.class.getSimpleName();
    private static boolean multiWindowEnabled = true;

    public static void launchScript(Context context, Project p) {

        /*
        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentId("launched script"));
        */

        Map<String, Object> map = AppRunnerHelper.readProjectProperties(context, p);
        boolean isService = (boolean) map.get("background_service");

        if (isService) {
            Intent intent = new Intent(context, AppRunnerService.class);
            intent.putExtra(Project.FOLDER, p.getFolder());
            intent.putExtra(Project.NAME, p.getName());
            intent.putExtra(Project.SERVER_PORT, PhonkSettings.HTTP_PORT);
            intent.putExtra("device_id", (String) UserPreferences.getInstance().get("device_id"));
            context.startService(intent);
        } else {
            Intent intent = new Intent(context, AppRunnerActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Project.FOLDER, p.getFolder());
            intent.putExtra(Project.NAME, p.getName());
            intent.putExtra(Project.SERVER_PORT, PhonkSettings.HTTP_PORT);
            intent.putExtra("device_id", (String) UserPreferences.getInstance().get("device_id"));
            MLog.d(TAG, "1 ------------> launching side by side " + AndroidUtils.isVersionN());

            if (AndroidUtils.isVersionN() && multiWindowEnabled) {
                MLog.d(TAG, "2 ------------> launching side by side");
                // intent.addFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT);
            }
            context.startActivity(intent);
        }
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

    public static void newProjectDialog(final MainActivity c) {

        FragmentManager fm = c.getSupportFragmentManager();
        NewProjectDialogFragment newProjectDialog = new NewProjectDialogFragment();
        newProjectDialog.show(fm, "fragment_edit_name");

        String[] templates = PhonkScriptHelper.listTemplates(c);
        for (String template : templates) {
            MLog.d(TAG, "template " + template);
        }

        newProjectDialog.setListener(new NewProjectDialogFragment.NewProjectDialogListener() {
            @Override
            public void onFinishEditDialog(String inputText) {
                String template = "default";
                Toast.makeText(c, "Creating " + inputText, Toast.LENGTH_SHORT).show();
                Project p = PhonkScriptHelper.createNewProject(c, template, "user_projects/User Projects/", inputText);
                EventBus.getDefault().post(new Events.ProjectEvent(Events.PROJECT_NEW, p));
            }
        });
    }


    public static void openInWebEditor(final Context c, Project p) {
        Intent i = new Intent("io.phonk.intent.WEBEDITOR_SEND");
        MLog.d("qq22", "openInWebEditor " + c);

        String[] splitted = p.getFolder().split("/"); // separating type and folder
        i.putExtra("action", "load_project");
        i.putExtra("type", splitted[0]);
        i.putExtra("folder", splitted[1]);
        i.putExtra("name", p.getName());
        c.sendBroadcast(i);
    }
}
