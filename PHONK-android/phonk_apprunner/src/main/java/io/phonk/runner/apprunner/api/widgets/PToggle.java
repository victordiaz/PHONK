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
import android.graphics.Typeface;
import android.view.View;

import java.util.Map;

import io.phonk.runner.apidoc.annotation.PhonkClass;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.api.common.ReturnInterface;
import io.phonk.runner.apprunner.api.common.ReturnObject;
import io.phonk.runner.base.utils.MLog;

@PhonkClass
public class PToggle extends androidx.appcompat.widget.AppCompatToggleButton implements PViewMethodsInterface,
        PTextInterface {
    private static final String TAG = PToggle.class.getSimpleName();

    public final StylePropertiesProxy props = new StylePropertiesProxy();
    private final ToggleStyler styler;
    private Typeface mFont;
    private int mStyle;

    public PToggle(AppRunner appRunner, Map initProps) {
        super(appRunner.getAppContext());

        styler = new ToggleStyler(appRunner, this, props);
        props.eventOnChange = false;
        props.put("textColor", props, appRunner.pUi.theme.get("primary"));
        props.put("background", props, "#00FFFFFF");
        props.put("backgroundChecked", props, appRunner.pUi.theme.get("primaryShade"));
        // props.put("backgroundPressed", props, appRunner.pUi.theme.get("textPrimary"));
        props.put("borderColor", props, appRunner.pUi.theme.get("secondaryShade"));
        props.put("borderWidth", props, appRunner.pUtil.dpToPixels(1));
        props.put("textOnColor", props, appRunner.pUi.theme.get("textPrimary"));
        props.put("textOffColor", props, appRunner.pUi.theme.get("textPrimary"));
        Styler.fromTo(initProps, props);
        props.eventOnChange = true;
        styler.apply();

        setText("Toggle");
        setTextOn("ON");
        setTextOff("OFF");
    }

    @Override
    public PToggle textFont(Typeface font) {
        mFont = font;
        this.setTypeface(font, mStyle);
        MLog.d(TAG, "--> " + "font");
        return this;
    }

    @Override
    public View textSize(int size) {
        this.setTextSize(size);
        return this;
    }

    @Override
    public View textColor(String textColor) {
        this.setTextColor(Color.parseColor(textColor));
        return this;
    }

    @Override
    public View textColor(int c) {
        this.setTextColor(c);
        return this;
    }

    @Override
    public View textSize(float textSize) {
        this.setTextSize(textSize);
        return this;
    }

    @Override
    public View textStyle(int style) {
        this.mStyle = style;
        this.setTypeface(this.mFont, style);
        return this;
    }

    @Override
    public View textAlign(int alignment) {
        setGravity(alignment);
        return this;
    }

    public PToggle onChange(final ReturnInterface callbackfn) {
        // Add change listener
        this.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {
                setTextColor(styler.textOnColor);
                styler.mBackgroundDrawable.setBackground(styler.backgroundCheckedColor);
            } else {
                setTextColor(styler.textOffColor);
                styler.mBackgroundDrawable.setBackground(styler.backgroundColor);
            }

            ReturnObject r = new ReturnObject(PToggle.this);
            r.put("checked", isChecked);
            callbackfn.event(r);
        });

        return this;
    }

    public View text(String label) {
        setText(label);
        setTextOn(label);
        setTextOff(label);
        return this;
    }

    public View checked(boolean b) {
        setChecked(b);
        return this;
    }

    @Override
    public void set(float x, float y, float w, float h) {
        styler.setLayoutProps(x, y, w, h);
    }

    class ToggleStyler extends Styler {
        int textOnColor;
        int textOffColor;
        int backgroundColor;
        int backgroundCheckedColor;

        ToggleStyler(AppRunner appRunner, View view, StylePropertiesProxy props) {
            super(appRunner, view, props);
        }

        @Override
        public void apply() {
            super.apply();

            textOnColor = Color.parseColor(mProps.get("textOnColor").toString());
            textOffColor = Color.parseColor(mProps.get("textOffColor").toString());
            backgroundColor = Color.parseColor(mProps.get("background").toString());
            backgroundCheckedColor = Color.parseColor(mProps.get("backgroundChecked").toString());

            if (isChecked()) {
                setTextColor(textOnColor);
                styler.mBackgroundDrawable.setBackground(backgroundCheckedColor);
            } else {
                setTextColor(textOffColor);
                styler.mBackgroundDrawable.setBackground(backgroundColor);
            }
        }
    }

    @Override
    public void setProps(Map style) {
        styler.setProps(style);
    }

    @Override
    public Map getProps() {
        return props;
    }
}
