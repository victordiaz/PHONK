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
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.VideoView;

import io.phonk.BuildConfig;
import io.phonk.R;
import io.phonk.gui._components.APIWebviewFragment;
import io.phonk.runner.base.BaseActivity;
import io.phonk.runner.base.utils.MLog;

public class AboutActivity extends BaseActivity {

    private static final String TAG = AboutActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity);

        setupActivity();

        ImageButton btnPatreon = findViewById(R.id.btnPatreon);
        ImageButton btnBuyMeACoffee = findViewById(R.id.btnBuyMeACoffee);

        btnPatreon.setOnClickListener(view -> openLink("https://www.patreon.com/victornomad"));
        btnBuyMeACoffee.setOnClickListener(view -> openLink("https://www.buymeacoffee.com/victordiaz"));

        TextView phonkVersionName = findViewById(R.id.versionName);
        phonkVersionName.setText(BuildConfig.VERSION_NAME);

        VideoView videoView = findViewById(R.id.videoView);
        String videoURI = "android.resource://" + getPackageName() + "/" + R.raw.phonk;
        videoView.setVideoURI(Uri.parse(videoURI));
        // videoView.setBackgroundColor(Color.WHITE);

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setLooping(true);
                videoView.setBackgroundColor(Color.TRANSPARENT);
                // videoView.setZOrderOnTop(true);
            }
        });
        videoView.start();
    }

    private void openLink(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @Override
    protected void setupActivity() {
        super.setupActivity();

        enableBackOnToolbar();
    }
}
