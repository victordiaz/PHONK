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

public class PLinearLayout extends LinearLayout implements PViewMethodsInterface {

    public final PropertiesProxy props = new PropertiesProxy();
    public final Styler styler;
    private final AppRunner mAppRunner;
    private final LayoutParams mLp;
    private final HashMap<String, View> mViews = new HashMap<>();

    private int horizontalAlign = Gravity.LEFT;
    private int verticalAlign = Gravity.TOP;

    public PLinearLayout(AppRunner appRunner, Map initProps) {
        super(appRunner.getAppContext());
        mAppRunner = appRunner;

        styler = new Styler(appRunner, this, props);
        props.onChange((name, value) -> {
            WidgetHelper.applyViewParam(name, value, props, this, appRunner);
            styler.apply(name, value);
            apply(name, value);
        });

        props.eventOnChange = false;
        props.put("orientation", "vertical");
        WidgetHelper.fromTo(initProps, props);
        props.eventOnChange = true;
        props.change();

        mLp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1f);

        setLayoutParams(mLp);
    }

    public PLinearLayout orientation(String orientation) {
        props.put("orientation", orientation);
        return this;
    }

    @PhonkMethod(description = "", example = "")
    @PhonkMethodParam(params = {""})
    public PLinearLayout add(View v, String name) {
        return add(v, name, 0);
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
        props.put("horizontalAlign", horizontal);
        props.put("verticalAlign", vertical);
    }

    public void padding(float l, float t, float r, float b) {
        props.eventOnChange = false;
        props.put("paddingLeft", l);
        props.put("paddingTop", t);
        props.put("paddingRight", r);
        props.eventOnChange = true;
        props.put("paddingBottom", b);
    }

    @PhonkMethod(description = "", example = "")
    @PhonkMethodParam(params = {""})
    public void clear() {
        removeAllViews();
    }

    public void background(int r, int g, int b) {
        props.put("background", String.format("#%02X%02X%02X", r, g, b));
    }

    private void apply(String name, Object value) {
        if (name == null) {
            apply("orientation");
            apply("horizontalAlign");
            apply("verticalAlign");

        } else {
            if (value == null) return;
            switch (name) {
                case "orientation":
                    setOrientation("horizontal".equals(value.toString()) ? HORIZONTAL : VERTICAL);
                    break;

                case "horizontalAlign":
                    switch (value.toString()) {
                        case "left":
                            horizontalAlign = Gravity.LEFT;
                            break;
                        case "center":
                            horizontalAlign = Gravity.CENTER_HORIZONTAL;
                            break;
                        case "right":
                            horizontalAlign = Gravity.RIGHT;
                            break;
                    }
                    setGravity(horizontalAlign | verticalAlign);
                    break;

                case "verticalAlign":
                    switch (value.toString()) {
                        case "top":
                            verticalAlign = Gravity.TOP;
                            break;
                        case "center":
                            verticalAlign = Gravity.CENTER_VERTICAL;
                            break;
                        case "bottom":
                            verticalAlign = Gravity.BOTTOM;
                            break;
                    }
                    setGravity(horizontalAlign | verticalAlign);
                    break;
            }
        }
    }

    private void apply(String name) {
        apply(name, props.get(name));
    }

    @Override
    public void set(float x, float y, float w, float h) {
        styler.setLayoutProps(x, y, w, h);
    }

    @Override
    public Map getProps() {
        return props;
    }

    @Override
    public void setProps(Map props) {
        WidgetHelper.setProps(this.props, props);
    }

}
