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

package io.phonk.gui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Map;

import io.phonk.R;
import io.phonk.runner.apprunner.AppRunnerHelper;
import io.phonk.runner.base.BaseActivity;
import io.phonk.runner.base.utils.MLog;
import io.phonk.runner.models.Project;

public class InfoScriptActivity extends BaseActivity {

    private static final String TAG = InfoScriptActivity.class.getSimpleName();
    private Project mCurrentProject;
    private LinearLayout mLicenseList;
    // private TextView mProjectNameTxt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.infoscript_activity);
        setupActivity();

        Intent intent = getIntent();
        if (null != intent) {
            String folder = intent.getStringExtra(Project.FOLDER);
            String name = intent.getStringExtra(Project.NAME);
            mCurrentProject = new Project(folder, name);
        }

        mLicenseList = findViewById(R.id.properties_list);
        // mProjectNameTxt = (TextView) findViewById(R.id.projectName);
        // mProjectNameTxt.setText("Project Info: " + mCurrentProject.name);

        getSupportActionBar().setTitle(mCurrentProject.name + " (app.conf)");

        Map<String, Object> map = AppRunnerHelper.readProjectProperties(this, mCurrentProject);

        if (map != null) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                MLog.d(TAG, entry.getKey() + entry.getValue().toString());

                View v = getLayoutInflater().inflate(R.layout.infoscript_keyvalue_view, null);
                TextView txtKey = v.findViewById(R.id.key);
                EditText txtValue = v.findViewById(R.id.value);
                txtKey.setText(entry.getKey());
                txtValue.setText(entry.getValue().toString());

                mLicenseList.addView(v);
            }
        }
    }

    @Override
    protected void setupActivity() {
        super.setupActivity();

        enableBackOnToolbar();
    }

}
