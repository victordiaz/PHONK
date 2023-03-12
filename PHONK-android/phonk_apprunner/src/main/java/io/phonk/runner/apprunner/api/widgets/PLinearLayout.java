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

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import java.util.HashMap;
import java.util.Map;

import io.phonk.runner.apidoc.annotation.PhonkMethod;
import io.phonk.runner.apidoc.annotation.PhonkMethodParam;
import io.phonk.runner.apprunner.AppRunner;

public class PLinearLayout extends LinearLayout {

    public final PropertiesProxy props = new PropertiesProxy();
    public final Styler styler;
    private final AppRunner mAppRunner;
    private final LayoutParams mLp;
    private final HashMap<String, View> mViews = new HashMap<>();

    public PLinearLayout(AppRunner appRunner, Map initProps) {
        super(appRunner.getAppContext());
        mAppRunner = appRunner;

        styler = new Styler(appRunner, this, props);
        props.onChange((name, value) -> {
            WidgetHelper.applyLayoutParams(name, value, props, this, appRunner);
            styler.apply(name, value);
        });

        props.eventOnChange = false;
        WidgetHelper.fromTo(initProps, props);
        props.eventOnChange = true;
        props.change();

        mLp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1f);
        orientation("vertical");

        setLayoutParams(mLp);
    }

    public PLinearLayout orientation(String orientation) {
        setOrientation("horizontal".equals(orientation) ? HORIZONTAL : VERTICAL);
        return this;
    }

    @PhonkMethod(description = "", example = "")
    @PhonkMethodParam(params = {""})
    public PLinearLayout add(View v, String name) {
        addView(v);
        mViews.put(name, v);
        return this;
    }

    public PLinearLayout add(View v, String name, float weight) {
        return add(v, name, LayoutParams.WRAP_CONTENT, weight);
    }

    public PLinearLayout add(View v, String name, Object height, float weight) {
        return add(v, name, LayoutParams.WRAP_CONTENT, height, weight);
    }

    public PLinearLayout add(View v, String name, Object width, Object height, float weight) {
        if (v instanceof PViewMethodsInterface) {
            PropertiesProxy props = (PropertiesProxy) ((PViewMethodsInterface) v).getProps();
            props.eventOnChange = false;
            props.put("w", width);
            props.put("h", height);
            props.eventOnChange = true;
        }

        int w = mAppRunner.pUtil.sizeToPixels(width, mAppRunner.pUi.screenWidth);
        int h = mAppRunner.pUtil.sizeToPixels(height, mAppRunner.pUi.screenHeight);

        if (w < 0) w = LayoutParams.WRAP_CONTENT;
        if (h < 0) h = LayoutParams.WRAP_CONTENT;

        // lp.gravity = Gravity.CENTER;
        LinearLayout.LayoutParams lp = new LayoutParams(w, h, weight);

        mViews.put(name, v);

        // setWeightSum(1.0f);
        addView(v, lp);
        return this;
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

    @PhonkMethod(description = "", example = "")
    @PhonkMethodParam(params = {""})
    public void clear() {
        removeAllViews();
    }

    public void background(int r, int g, int b) {
        setBackgroundColor(Color.rgb(r, g, b));
    }

}
