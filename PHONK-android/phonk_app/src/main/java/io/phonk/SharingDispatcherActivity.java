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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;

import java.util.HashMap;
import java.util.Map;

import io.phonk.gui.projectbrowser.ProjectBrowserDialogFragment;
import io.phonk.gui.projectbrowser.projectlist.ProjectItem;
import io.phonk.gui.projectbrowser.projectlist.ProjectListFragment;
import io.phonk.helpers.PhonkAppHelper;
import io.phonk.runner.base.BaseActivity;
import io.phonk.runner.base.models.Project;
import io.phonk.runner.base.utils.MLog;

public class SharingDispatcherActivity extends BaseActivity {

    final HashMap<String, String> extras = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setFinishOnTouchOutside(false);

        // setContentView(R.layout.license_view);

        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            handleSend(intent, type); // Handle text being sent
        }

        // TODO manage handling multiple items
        // if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
        // ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);

        // setContentView(R.layout.new_main_activity);

        Button btn = findViewById(R.id.btnOpenProjectBrowser);

        FragmentManager fm = getSupportFragmentManager();
        ProjectBrowserDialogFragment projectBrowserDialogFragment = ProjectBrowserDialogFragment.newInstance(ProjectItem.MODE_SINGLE_PICK);

        projectBrowserDialogFragment.setListener(new ProjectListFragment.ProjectSelectedListener() {
            @Override
            public void onProjectSelected(Project p) {
                projectBrowserDialogFragment.dismiss();
                PhonkAppHelper.launchScript(SharingDispatcherActivity.this, new Project(p.getSandboxPath()), extras);
                // PhonkAppHelper.launchScript(this, new Project("examples/Graphical User Interface/Basic Views"));
                SharingDispatcherActivity.this.finish();

                Toast.makeText(getApplicationContext(), "Sending to " + p.getSandboxPath(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onMultipleProjectsSelected(HashMap<Project, Boolean> projects) {
                for (Map.Entry<Project, Boolean> entry : projects.entrySet()) {
                    Project project = entry.getKey();
                    boolean selection = entry.getValue();
                }
            }

            @Override
            public void onActionClicked(String action) {

            }
        });

        projectBrowserDialogFragment.show(fm, "project_browser_dialog_fragment");
    }

    void handleSend(Intent intent, String type) {

        if (type.startsWith("text/")) {
            String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
            if (sharedText == null) return;
            extras.put("shareType", type);
            extras.put("shareContent", sharedText);

        } else if (type.startsWith("image/")) {
            Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            if (imageUri == null) return;
            extras.put("shareType", type);
            extras.put("shareContent", imageUri.toString());
        }

        // PhonkAppHelper.launchScript(this, new Project("playground/User Projects/activity"), extras);
    }

}
