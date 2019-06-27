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

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import io.phonk.R;
import io.phonk.gui._components.APIWebviewFragment;
import io.phonk.runner.base.BaseActivity;

public class HelpActivity extends BaseActivity {

    private static final String TAG = HelpActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_activity);

        setupActivity();

        FrameLayout fl = findViewById(R.id.fragmentWebview2);
        fl.setVisibility(View.VISIBLE);
        APIWebviewFragment webViewFragment = new APIWebviewFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", "file:///android_asset/help.md.html"); // http://127.0.0.1:8585");
        webViewFragment.setArguments(bundle);
        addFragment(webViewFragment, R.id.fragmentWebview2, "qq");
    }

    @Override
    protected void setupActivity() {
        super.setupActivity();

        enableBackOnToolbar();
    }
}
