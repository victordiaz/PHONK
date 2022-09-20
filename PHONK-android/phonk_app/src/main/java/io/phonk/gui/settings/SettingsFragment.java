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

package io.phonk.gui.settings;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.TwoStatePreference;

import java.util.HashMap;

import io.phonk.BuildConfig;
import io.phonk.R;
import io.phonk.gui.LicenseActivity;
import io.phonk.gui.projectbrowser.ProjectBrowserDialogFragment;
import io.phonk.gui.projectbrowser.projectlist.ProjectItem;
import io.phonk.gui.projectbrowser.projectlist.ProjectListFragment;
import io.phonk.helpers.PhonkAppHelper;
import io.phonk.helpers.PhonkSettingsHelper;
import io.phonk.runner.base.models.Project;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class SettingsFragment extends PreferenceFragmentCompat {

    protected static final String TAG = SettingsFragment.class.getSimpleName();
    private static final int REQUEST_OVERLAY_PERMISSIONS = 12312;
    private final String TAG_PROJECT_LAUNCH_ON_PHONK_START = "launch_script_on_app_launch";
    private final String TAG_PROJECT_LAUNCH_ON_BOOT = "launch_script_on_boot";

    private Context mContext;
    private UserPreferences mUserPreferences;
    private View mParentView;

    private Preference prefLaunchScriptOnStart;
    private Preference prefLaunchScriptOnBoot;

    public SettingsFragment() {

    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    //twostatepreference(boolean)->action/action edittextpreference(text)->action preference->action
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mParentView = super.onCreateView(inflater, container, savedInstanceState);

        mContext = getActivity();
        mUserPreferences = UserPreferences.getInstance();

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        FragmentManager fm = getChildFragmentManager();
        ProjectBrowserDialogFragment projectBrowserDialogFragment = ProjectBrowserDialogFragment.newInstance(ProjectItem.MODE_SINGLE_PICK_CLEAR);
        projectBrowserDialogFragment.setListener(new ProjectListFragment.ProjectSelectedListener() {
            @Override
            public void onProjectSelected(Project p) {
                if (projectBrowserDialogFragment.getTag().equals(TAG_PROJECT_LAUNCH_ON_PHONK_START)) {
                    prefLaunchScriptOnStart.setSummary(p.getSandboxPath());
                    mUserPreferences.set(TAG_PROJECT_LAUNCH_ON_PHONK_START, p.getSandboxPath()).save();
                } else if (projectBrowserDialogFragment.getTag().equals(TAG_PROJECT_LAUNCH_ON_BOOT)) {
                    prefLaunchScriptOnBoot.setSummary(p.getSandboxPath());
                    mUserPreferences.set(TAG_PROJECT_LAUNCH_ON_BOOT, p.getSandboxPath()).save();
                }
                projectBrowserDialogFragment.dismiss();

                Toast.makeText(getContext(), "Sending to " + p.getFullPath(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onMultipleProjectsSelected(HashMap<Project, Boolean> projects) {
            }

            @Override
            public void onActionClicked(String action) {
                if (projectBrowserDialogFragment.getTag().equals(TAG_PROJECT_LAUNCH_ON_PHONK_START)) {
                    if (action.equals("clear")) {
                        prefLaunchScriptOnStart.setSummary(getString(R.string.setting_launch_script_on_app_launch_description));
                        mUserPreferences.set(TAG_PROJECT_LAUNCH_ON_PHONK_START, "").save();
                    }
                } else if (projectBrowserDialogFragment.getTag().equals(TAG_PROJECT_LAUNCH_ON_BOOT)) {
                    if (action.equals("clear")) {
                        prefLaunchScriptOnBoot.setSummary(getString(R.string.setting_launch_script_on_boot_description));
                        mUserPreferences.set(TAG_PROJECT_LAUNCH_ON_BOOT, "").save();
                    }
                }
            }
        });

        final EditTextPreference prefDeviceId = findPreference("device_id");
        prefDeviceId.setOnPreferenceChangeListener((preference, newValue) -> {
            prefDeviceId.setText((String) newValue);
            mUserPreferences.set("device_id", newValue).save();
            return false;
        });
        prefDeviceId.setText((String) UserPreferences.getInstance().get("device_id"));

        // Screen always on mode
        final TwoStatePreference prefScreenOn = findPreference("screen_always_on");
        prefScreenOn.setOnPreferenceChangeListener((preference, o) -> {
            boolean isChecked = (Boolean) o;
            mUserPreferences.set("screen_always_on", isChecked).save();
            return true;
        });
        prefScreenOn.setChecked((Boolean) mUserPreferences.get("screen_always_on"));

        // Start servers on launch
        final TwoStatePreference prefStartServers = findPreference("servers_enabled_on_start");
        prefStartServers.setOnPreferenceChangeListener((preference, o) -> {
            boolean isChecked = (Boolean) o;
            mUserPreferences.set("servers_enabled_on_start", isChecked).save();
            return true;
        });
        prefStartServers.setChecked((Boolean) mUserPreferences.get("servers_enabled_on_start"));

        // Wake up device on play
        final TwoStatePreference prefWakeUpOnPlay = findPreference("device_wakeup_on_play");
        prefWakeUpOnPlay.setOnPreferenceChangeListener((preference, o) -> {
            boolean isChecked = (Boolean) o;
            mUserPreferences.set("device_wakeup_on_play", isChecked).save();
            return true;
        });
        prefWakeUpOnPlay.setChecked((Boolean) mUserPreferences.get("device_wakeup_on_play"));

        // Start servers on launch
        final TwoStatePreference prefMaskIp = findPreference("servers_mask_ip");
        prefMaskIp.setOnPreferenceChangeListener((preference, o) -> {
            boolean isChecked = (Boolean) o;
            mUserPreferences.set("servers_mask_ip", isChecked).save();
            return true;
        });
        prefMaskIp.setChecked((Boolean) mUserPreferences.get("servers_mask_ip"));

        // Advertise mDNS
        final TwoStatePreference prefMDNS = findPreference("advertise_mdns");
        prefMDNS.setOnPreferenceChangeListener((preference, o) -> {
            boolean isChecked = (Boolean) o;
            mUserPreferences.set("advertise_mdns", isChecked).save();
            return true;
        });
        prefMDNS.setChecked((Boolean) mUserPreferences.get("advertise_mdns"));

        // Notify new version
        /*
        final TwoStatePreference prefNewVersionCheck = (TwoStatePreference) findPreference("notify_new_version");
        prefNewVersionCheck.setOnPreferenceChangeListener((preference, o) -> {
            boolean isChecked = (Boolean) o;
            mUserPreferences.set("notify_new_version", isChecked).save();
            return true;
        });
        prefNewVersionCheck.setChecked((Boolean) mUserPreferences.get("notify_new_version"));
         */

        /*
        // Send usage log
        final TwoStatePreference prefSendUsageLog = (TwoStatePreference) findPreference("send_usage_log");
        prefSendUsageLog.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                boolean isChecked = (Boolean) o;
                mUserPreferences.set("send_usage_log", isChecked).save();
                return true;
            }
        });
        prefSendUsageLog.setChecked((Boolean) mUserPreferences.get("send_usage_log"));
        */

        // WebIDE mode
        final TwoStatePreference prefWebIdeMode = findPreference("webide_mode");
        prefWebIdeMode.setOnPreferenceChangeListener((preference, o) -> {
            boolean isChecked = (Boolean) o;
            mUserPreferences.set("webide_mode", isChecked).save();

            PhonkSettingsHelper.showRestartMessage(mContext, mParentView);
            return true;
        });
        prefWebIdeMode.setChecked((Boolean) mUserPreferences.get("webide_mode"));


        /**
         * Launch PHONK on boot
         */
        // Launch on device boot mode
        final TwoStatePreference prefLaunchOnBoot = findPreference("launch_on_device_boot");
        prefLaunchOnBoot.setOnPreferenceChangeListener((preference, o) -> {
            boolean drawOverlayIsAllowed = launchSettingsDrawOverlay();

            if (drawOverlayIsAllowed) {
                boolean isChecked = (Boolean) o;
                mUserPreferences.set("launch_on_device_boot", isChecked).save();
            } else {
                Toast.makeText(getActivity(), "You need to allow the permission first", Toast.LENGTH_LONG).show();
            }
            return true;
        });
        prefLaunchOnBoot.setChecked((Boolean) mUserPreferences.get("launch_on_device_boot"));


        /**
         * Launch script when PHONK starts
         */
        prefLaunchScriptOnStart = findPreference("launch_script_on_app_launch");
        prefLaunchScriptOnStart.setOnPreferenceClickListener(preference -> {
            projectBrowserDialogFragment.show(fm, TAG_PROJECT_LAUNCH_ON_PHONK_START);
            return true;
        });
        String scriptOnStartSummary = (String) mUserPreferences.get(TAG_PROJECT_LAUNCH_ON_PHONK_START);
        if (!scriptOnStartSummary.isEmpty()) {
            prefLaunchScriptOnStart.setSummary(scriptOnStartSummary);
        }

        /**
         * Launch script when PHONK boots
         */
        prefLaunchScriptOnBoot = findPreference("launch_script_on_boot");
        prefLaunchScriptOnBoot.setOnPreferenceClickListener(preference -> {
            launchSettingsDrawOverlay();
            boolean drawOverlayIsAllowed = launchSettingsDrawOverlay();

            if (drawOverlayIsAllowed) {
                projectBrowserDialogFragment.show(fm, TAG_PROJECT_LAUNCH_ON_BOOT);
            } else {
                Toast.makeText(getActivity(), "You need to allow the permission first", Toast.LENGTH_LONG).show();
            }
            return true;
        });
        String scriptOnBootSummary = (String) mUserPreferences.get(TAG_PROJECT_LAUNCH_ON_BOOT);
        if (!scriptOnBootSummary.isEmpty()) {
            prefLaunchScriptOnBoot.setSummary(scriptOnBootSummary);
        }

        Preference phonkVersionName = findPreference("phonkVersionName");
        phonkVersionName.setSummary(BuildConfig.VERSION_NAME);

        Preference btnShowLicenses = findPreference("licenses_detail");
        btnShowLicenses.setOnPreferenceClickListener(arg0 -> {
            startActivity(new Intent(getActivity(), LicenseActivity.class));
            return true;
        });

        Preference btnReinstall = findPreference("reinstall_examples");
        btnReinstall.setOnPreferenceClickListener(arg0 -> {
            final ProgressDialog progress = new ProgressDialog(getActivity());
            progress.setTitle("Reinstalling examples");
            progress.setMessage("Your examples are getting restored, wait a sec!");
            progress.setCancelable(false);
            progress.setCanceledOnTouchOutside(false);

            new AlertDialog.Builder(getActivity()).setMessage("Do you really want to reinstall the examples?")
                    .setCancelable(false).setPositiveButton("Yes", (dialog, which) -> {
                        progress.show();

                        PhonkSettingsHelper.installExamples(getActivity(), () -> progress.dismiss());
                        dialog.cancel();
                    }).setNegativeButton("No", (dialog, which) -> dialog.cancel()).show();

            return true;
        });

        Preference btnRequestPermissions = findPreference("request_permissions");
        btnRequestPermissions.setOnPreferenceClickListener(preference -> {
            PhonkAppHelper.launchAppSettings(getActivity());
            return true;
        });


        //load webIDE
        // TODO enable again
        /*

        // Notify and download
        final TwoStatePreference notifyNewVersionPreference = (TwoStatePreference) findPreference("pref_notify_new_version");
        if (notifyNewVersionPreference != null) {
            notifyNewVersionPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    boolean isChecked = (Boolean) o;
                    mPrefs.edit().putBoolean("pref_notify_new_version", isChecked).commit();
                    return true;
                }
            });
        }
        */

        return mParentView;
    }

    public boolean launchSettingsDrawOverlay() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(getActivity().getApplicationContext())) {
                Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);

                myIntent.setData(uri);
                startActivityForResult(myIntent, REQUEST_OVERLAY_PERMISSIONS);

                return false;
            }
        }
        return true;
    }
}
