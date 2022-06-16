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

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;

import java.util.HashMap;
import java.util.Map;

import io.phonk.gui.projectbrowser.ProjectBrowserDialogFragment;
import io.phonk.gui.projectbrowser.projectlist.ProjectItem;
import io.phonk.gui.projectbrowser.projectlist.ProjectListFragment;
import io.phonk.runner.base.BaseActivity;
import io.phonk.runner.base.models.Project;
import io.phonk.runner.base.utils.MLog;

public class NewMainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.new_main_activity);

        Button btn = findViewById(R.id.btnOpenProjectBrowser);

        FragmentManager fm = getSupportFragmentManager();
        ProjectBrowserDialogFragment projectBrowserDialogFragment = ProjectBrowserDialogFragment.newInstance(ProjectItem.MODE_SINGLE_PICK);

        projectBrowserDialogFragment.setListener(new ProjectListFragment.ProjectSelectedListener() {
            @Override
            public void onProjectSelected(Project p) {
                projectBrowserDialogFragment.dismiss();
                Toast.makeText(getApplicationContext(), "Sending to " + p.getFullPath(), Toast.LENGTH_LONG).show();
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

        btn.setOnClickListener(view -> {
            projectBrowserDialogFragment.show(fm, "project_browser_dialog_fragment");
        });
    }
}
