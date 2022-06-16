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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.phonk.BuildConfig;
import io.phonk.MainActivity;
import io.phonk.R;
import io.phonk.gui.settings.PhonkSettings;
import io.phonk.helpers.PhonkAppHelper;
import io.phonk.helpers.PhonkSettingsHelper;
import io.phonk.runner.base.BaseActivity;

public class UpdateActivity extends BaseActivity {

    private static final String TAG = UpdateActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_activity);

        setupActivity();

        TextView txtVersionLine = findViewById(R.id.txtVersionLine);
        txtVersionLine.setText(Html.fromHtml(txtVersionLine.getText().toString().replace("###", "<font color = #FFC700>" + BuildConfig.VERSION_NAME.split("_")[0] + "</font>")));

        String changelog = PhonkAppHelper.readFile(this, "changelog.txt");
        RecyclerView recyclerChangelog = findViewById(R.id.recyclerChangelog);
        recyclerChangelog.setLayoutManager(new LinearLayoutManager(this));
        MyAdapter myAdapter = new MyAdapter(changelog);
        recyclerChangelog.setAdapter(myAdapter);

        Button btnSkip = findViewById(R.id.btnSkip);
        Button btnReinstall = findViewById(R.id.btnReinstall);
        Button btnDone = findViewById(R.id.btnDone);

        LinearLayout lLoading = findViewById(R.id.loading);

        btnDone.setOnClickListener(view -> {
           ready();
        });

        btnSkip.setOnClickListener(view -> {
            SharedPreferences userDetails = getSharedPreferences("io.phonk", MODE_PRIVATE);
            userDetails.edit().putInt("last_version_reinstalled", BuildConfig.VERSION_CODE).commit();
            ready();
        });

        btnReinstall.setOnClickListener(view -> {
            btnSkip.setVisibility(View.INVISIBLE);
            btnReinstall.setVisibility(View.INVISIBLE);

            lLoading.setVisibility(View.VISIBLE);

            PhonkSettingsHelper.installExamples(getApplicationContext(), PhonkSettings.EXAMPLES_FOLDER, () -> runOnUiThread(() -> {
                lLoading.setVisibility(View.GONE);
                btnDone.setVisibility(View.VISIBLE);

                SharedPreferences userDetails = getSharedPreferences("io.phonk", MODE_PRIVATE);
                userDetails.edit().putInt("last_version_reinstalled", BuildConfig.VERSION_CODE).commit();
            }));
        });
    }

    public void ready() {
        // Start the activity
        Intent i = new Intent(UpdateActivity.this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }

    @Override
    protected void setupActivity() {
        super.setupActivity();
        mToolbar.setTitle("patata" + BuildConfig.VERSION_NAME);
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        private List<ChangeLogItem> changeLogList = new ArrayList<>();

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView txtVersion;
            public TextView txtChanges;

            public MyViewHolder(LinearLayout v) {
                super(v);
                txtVersion = v.findViewById(R.id.version);
                txtChanges = v.findViewById(R.id.changes);
            }
        }

        MyAdapter(String changelog) {
            String[] releases = changelog.split("\\n\\n");
            for (String release : releases) {
                ChangeLogItem changeLogItem = new ChangeLogItem(release);
                changeLogList.add(changeLogItem);
            }
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.update_activity_changelog_item, parent, false);
            MyViewHolder vh = new MyViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.txtVersion.setText(changeLogList.get(position).version);
            holder.txtChanges.setText(changeLogList.get(position).changes);
        }

        @Override
        public int getItemCount() {
            return changeLogList.size();
        }
    }

    class ChangeLogItem {
        String version;
        String changes;

        ChangeLogItem(String release) {
            String[] s = release.split("\n");
            this.version = s[0];
            this.changes = "";
            for (int i = 1; i < s.length; i++) {
                changes += s[i] + "\n";
            }
        }
    }
}
