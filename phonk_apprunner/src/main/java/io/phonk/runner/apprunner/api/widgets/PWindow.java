/*
 * Part of Phonk http://www.phonk.io
 * A prototyping platform for Android devices
 *
 * Copyright (C) 2013 - 2017 Victor Diaz Barrales @victordiaz (Protocoder)
 * Copyright (C) 2017 - Victor Diaz Barrales @victordiaz (Phonk)
 *
 * Phonk is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Phonk is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Phonk. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package io.phonk.runner.apprunner.api.widgets;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import io.phonk.runner.R;
import io.phonk.runner.apidoc.annotation.PhonkMethod;
import io.phonk.runner.apidoc.annotation.PhonkMethodParam;

public class PWindow extends RelativeLayout {
    private static final String TAG = PWindow.class.getSimpleName();

    private final int currentColor;
    private final int viewCount = 0;
    private final Context c;
    private final RelativeLayout mBar;
    private final LinearLayout mMainContainer;
    private final PWindow mWindow;
    private Button mBtnClose;
    private TextView mTitle;

    public PWindow(Context context) {
        super(context);
        c = context;
        currentColor = Color.argb(255, 255, 255, 255);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.pwidget_window, this, true);

        mWindow = this;
        mBar = findViewById(R.id.pWidgetWindowBar);
        mTitle = findViewById(R.id.pWidgetWindowTitle);
        mBtnClose = findViewById(R.id.pWidgetWindowClose);
        mMainContainer = findViewById(R.id.pWidgetWindowMainContainer);

        //setOnTouchListener(this);

        WidgetHelper.setMovable(mBar, mWindow, null);
    }


    @PhonkMethod(description = "Adds a new view", example = "")
    @PhonkMethodParam(params = {"view"})
    public PWindow addWidget(View v) {
        v.setAlpha(0);
        v.animate().alpha(1).setDuration(500).setStartDelay(100 * (1 + viewCount));

        mMainContainer.addView(v);

        return mWindow;
    }


    @PhonkMethod(description = "Show/hides the window bar", example = "")
    @PhonkMethodParam(params = {"boolean"})
    public PWindow showBar(boolean b) {
        if (b) {
            mBar.setVisibility(View.VISIBLE);
        } else {
            mBar.setVisibility(View.INVISIBLE);
        }

        return mWindow;

    }


    @PhonkMethod(description = "Sets the window title", example = "")
    @PhonkMethodParam(params = {"text"})
    public PWindow setTitle(String text) {
        mTitle.setText(text);

        return mWindow;

    }


    @PhonkMethod(description = "Sets the title color", example = "")
    @PhonkMethodParam(params = {"colorHext"})
    public PWindow setTitleColor(String color) {
        mTitle.setTextColor(Color.parseColor(color));

        return mWindow;
    }


    @PhonkMethod(description = "Sets the bar background color", example = "")
    @PhonkMethodParam(params = {"colorHex"})
    public PWindow setBarBackgroundColor(String color) {
        mBar.setBackgroundColor(Color.parseColor(color));

        return mWindow;
    }


    @PhonkMethod(description = "Sets the background color", example = "")
    @PhonkMethodParam(params = {"colorHex"})
    public PWindow setWindowBackgroundColor(String color) {
        mWindow.setBackgroundColor(Color.parseColor(color));

        return mWindow;
    }


}
