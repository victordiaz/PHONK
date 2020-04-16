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

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import io.phonk.R;
import io.phonk.runner.base.BaseActivity;

public class LicenseActivity extends BaseActivity {

    private static final java.lang.String TAG = LicenseActivity.class.getSimpleName();
    private AssetManager mAssetManager;
    private String[] mLicenseFiles;
    private ArrayList<License> mLicenseFileContent = new ArrayList<>();

    private ListView mLicenseList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.license_activity);
        mLicenseList = findViewById(R.id.license_list);

        final MyAdapter myAdapter = new MyAdapter(this, mLicenseFileContent);
        mLicenseList.setAdapter(myAdapter);

        setupActivity();

        mAssetManager = getAssets();

        final Handler handler = new Handler();

        Thread t = new Thread(() -> {

            // read mCurrentFileList
            try {
                 mLicenseFiles = mAssetManager.list("licenses");
                for (int i = 0; i < mLicenseFiles.length; i++) {
                    mLicenseFileContent.add(new License(mLicenseFiles[i], readFile("licenses/" + mLicenseFiles[i])));
                    // MLog.d(TAG, filecontent);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // show license in ui
            handler.post(() -> {
                for (int i = 0; i < mLicenseFiles.length; i++) {
                    View v = getLayoutInflater().inflate(R.layout.license_view, null);
                    TextView txtView = v.findViewById(R.id.license_title);
                    txtView.setText(mLicenseFiles[i]);

                    myAdapter.notifyDataSetChanged();
                    mLicenseList.invalidateViews();
                }
            });
        });
        t.start();

    }

    @Override
    protected void setupActivity() {
        super.setupActivity();

        enableBackOnToolbar();
    }

    private String readFile(String path) throws IOException {
        InputStream is = mAssetManager.open(path);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
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
        return byteArrayOutputStream.toString();
    }

    class License {
        public String title;
        public String body;
        public boolean showing = false;

        public License(String name, String content) {
            title = name.replace("_", " ").replace(".txt", "");
            body = content;
        }
    }

    private class MyAdapter extends ArrayAdapter<License> {

        public MyAdapter(Context context, ArrayList<License> strings) {
            super(context, -1, -1, strings);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.license_view, parent, false);
            }
            TextView txtTitle = convertView.findViewById(R.id.license_title);
            final TextView txtText = convertView.findViewById(R.id.license_body);

            final License license = getItem(position);
            txtTitle.setText(license.title);
            txtText.setText(license.body);

            txtText.setOnClickListener(view -> {
                if (!license.showing) {
                    txtText.setEllipsize(null);
                    txtText.setMaxLines(Integer.MAX_VALUE);
                }
                else {
                    txtText.setEllipsize(TextUtils.TruncateAt.END);
                    txtText.setMaxLines(3);
                }

                license.showing = !license.showing;
            });

            return convertView;
        }
    }

}
