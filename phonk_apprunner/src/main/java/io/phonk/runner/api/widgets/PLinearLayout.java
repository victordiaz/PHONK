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

package io.phonk.runner.api.widgets;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import java.util.HashMap;

import io.phonk.runner.apidoc.annotation.ProtoMethod;
import io.phonk.runner.apidoc.annotation.ProtoMethodParam;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.StyleProperties;

public class PLinearLayout extends LinearLayout {

    private final AppRunner mAppRunner;
    private final LayoutParams mLp;

    public StyleProperties props = new StyleProperties();
    public Styler styler;
    private HashMap<String, View> mViews = new HashMap<>();

    public PLinearLayout(AppRunner appRunner) {
        super(appRunner.getAppContext());
        mAppRunner = appRunner;

        styler = new Styler(appRunner, this, props);
        styler.apply();

        mLp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1f);
        setLayoutParams(mLp);
    }

    public void orientation(String orientation) {
        int mode = VERTICAL;
        switch (orientation) {
            case "horizontal":
                mode = HORIZONTAL;
                break;
        }
        setOrientation(mode);

    }
    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {""})
    public void add(View v, String name) {
        addView(v);
        mViews.put(name, v);
    }

    public void add(View v, String name, float weight) {
        add(v, name, LayoutParams.WRAP_CONTENT, weight);
    }

    public void add(View v, String name, float height, float weight) {
        // lp.gravity = Gravity.CENTER;
        LinearLayout.LayoutParams lp = new LayoutParams(0, (int) height, weight);

        mViews.put(name, v);

        // setWeightSum(1.0f);
        addView(v, lp);
    }

    public View get(String name) {
        return mViews.get(name);
    }

    public void alignViews(String horizontal, String vertical) {
        int h = Gravity.LEFT;
        switch (horizontal) {
            case "left":
                h = Gravity.LEFT;
                break;
            case "center":
                h = Gravity.CENTER_HORIZONTAL;
                break;
            case "right":
                h = Gravity.RIGHT;
                break;
        }

        int v = Gravity.TOP;
        switch (vertical) {
            case "top":
                v = Gravity.TOP;
                break;
            case "center":
                v = Gravity.CENTER_VERTICAL;
                break;
            case "bottom":
                v = Gravity.BOTTOM;
                break;
        }

        setGravity(h | v);
    }

    public void padding(float l, float t, float r, float b) {
        setPadding((int) l, (int) t, (int) r, (int) b);
    }

    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {""})
    public void clear() {
        removeAllViews();
    }

    public void background(int r, int g, int b) {
        setBackgroundColor(Color.rgb(r, g, b));
    }

}
