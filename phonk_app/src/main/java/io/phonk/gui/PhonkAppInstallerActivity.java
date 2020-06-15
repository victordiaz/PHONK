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
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import io.phonk.R;
import io.phonk.gui.settings.PhonkSettings;
import io.phonk.helpers.PhonkScriptHelper;
import io.phonk.runner.base.BaseActivity;
import io.phonk.runner.base.models.Project;
import io.phonk.runner.base.utils.MLog;

public class PhonkAppInstallerActivity extends BaseActivity {

    private static final String TAG = PhonkAppInstallerActivity.class.getSimpleName();

    ProgressBar mProgress;
    Button mBtnFinish;
    private LinearLayout mLl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_phonk_installer);

        TextView txtProto = findViewById(R.id.text_proto_install_info);
        TextView txtOrigin = findViewById(R.id.from_url);
        TextView txtDestiny = findViewById(R.id.to_url);
        TextView txtWarning = findViewById(R.id.text_proto_install_warning);

        mLl = findViewById(R.id.proto_install_group);
        Button btnInstall = findViewById(R.id.button_proto_install_ok);
        Button btnCancel = findViewById(R.id.button_proto_install_cancel);
        mBtnFinish = findViewById(R.id.button_proto_install_finish);
        mProgress = findViewById(R.id.progressBar_installing);

        Intent intent = getIntent();
        if (intent == null) {
            finish();
        }

        final Uri urlData = intent.getData();
        txtProto.setText("A project is going to be installed");
        txtOrigin.setText("from: " + urlData.getPath());

        PhonkSettings.getFolderPath(PhonkSettings.USER_PROJECTS_FOLDER);

        String folder = "playground/User Projects";
        File f = new File(urlData.getPath());

        int index = f.getName().lastIndexOf("_");
        String name = f.getName().substring(0, index); //replaceFirst("[.][^.]+$", "");
        final Project p = new Project(folder, name);
        txtDestiny.setText("to: " + p.getFullPath());

        //check if project is already installed
        if (p.exists()) {
            txtWarning.setVisibility(View.VISIBLE);
        }

        btnInstall.setOnClickListener(v -> install(p, urlData));

        btnCancel.setOnClickListener(v -> finish());

        mBtnFinish.setOnClickListener(v -> finish());


        //check if is autoinstall and proceed
        // Read in the script given in the intent.
        if (null != intent) {
            boolean autoInstall = intent.getBooleanExtra("autoInstall", false);

            if (autoInstall) {
                btnInstall.performClick();
                mBtnFinish.performClick();
            }
        }
    }

    private void install(Project p, Uri urlData) {
        mProgress.setVisibility(View.VISIBLE);

        if (urlData != null) {
            new File(p.getFullPath()).mkdirs();
            boolean ok = PhonkScriptHelper.installProject(this, urlData.getPath(), p.getFullPath());
            MLog.d(TAG, "installed " + ok);
            mProgress.setVisibility(View.GONE);

            mLl.setVisibility(View.GONE);
            mBtnFinish.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Proto app " + p.getName() + " installed", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
