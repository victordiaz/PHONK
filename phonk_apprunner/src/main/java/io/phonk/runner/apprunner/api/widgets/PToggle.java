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
import io.phonk.runner.apidoc.annotation.PhonkMethod;
import io.phonk.runner.apidoc.annotation.PhonkMethodParam;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.api.common.ReturnInterface;
import io.phonk.runner.apprunner.api.common.ReturnObject;
import io.phonk.runner.base.utils.MLog;

@PhonkClass
public class PToggle extends androidx.appcompat.widget.AppCompatToggleButton implements PViewMethodsInterface, PTextInterface {
    private static final String TAG = PToggle.class.getSimpleName();

    public StylePropertiesProxy props = new StylePropertiesProxy();
    private ToggleStyler styler;
    private Typeface mFont;

    public PToggle(AppRunner appRunner, Map initProps) {
        super(appRunner.getAppContext());

        styler = new ToggleStyler(appRunner, this, props);
        props.eventOnChange = false;
        props.put("textColor", props, appRunner.pUi.theme.get("primary"));
        props.put("background", props, "#00FFFFFF");
        props.put("backgroundChecked", props, appRunner.pUi.theme.get("secondaryShade"));
        // props.put("backgroundPressed", props, appRunner.pUi.theme.get("textPrimary"));
        props.put("borderColor", props, appRunner.pUi.theme.get("secondary"));
        props.put("borderWidth", props, appRunner.pUtil.dpToPixels(1));
        props.put("checked", props, false);
        props.put("text", props, "Toggle");
        props.put("textOn", props, "ON");
        props.put("textOff", props, "OFF");
        props.put("textOnColor", props, appRunner.pUi.theme.get("primary"));
        props.put("textOffColor", props, appRunner.pUi.theme.get("textPrimary"));
        styler.fromTo(initProps, props);
        props.eventOnChange = true;
        styler.apply();

        textFont(mFont);
    }

    public PToggle onChange(final ReturnInterface callbackfn) {
        // Add change listener
        this.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {
                setTextColor(styler.textOnColor);
            } else {
                setTextColor(styler.textOffColor);
            }

            ReturnObject r = new ReturnObject(PToggle.this);
            r.put("checked", isChecked);
            callbackfn.event(r);
        });

        return this;
    }

    public void text(String label) {
        this.props.put("text", this.props, label);
        setText(label);
        setTextOn(label);
        setTextOff(label);
    }

    public void checked(boolean b) {
        this.props.eventOnChange = false;
        this.props.put("checked", this.props, b);
        this.props.eventOnChange = true;
        setChecked(b);
    }

    @Override
    public PToggle textFont(Typeface font) {
        mFont = font;
        this.setTypeface(font);
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
    @PhonkMethod(description = "Changes the font text color", example = "")
    @PhonkMethodParam(params = {"colorHex"})
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
        this.setTypeface(mFont, style);
        return this;
    }

    @Override
    public View textAlign(int alignment) {
        setGravity(alignment);
        return this;
    }

    @Override
    public void set(float x, float y, float w, float h) {
        styler.setLayoutProps(x, y, w, h);
    }

    @Override
    public void setProps(Map style) {
        styler.setProps(style);
    }

    @Override
    public Map getProps() {
        return props;
    }

    class ToggleStyler extends Styler {
        private String textOn;
        private String textOff;
        private String text;
        private int textOnColor;
        private int textOffColor;

        ToggleStyler(AppRunner appRunner, View view, StylePropertiesProxy props) {
            super(appRunner, view, props);
        }

        @Override
        public void apply() {
            super.apply();

            checked((boolean) mProps.get("checked"));
            textOn = mProps.get("textOn").toString();
            textOff = mProps.get("textOff").toString();
            text = mProps.get("text").toString();

            setText(text);
            setTextOn(textOn);
            setTextOff(textOff);

            setTextColor(Color.parseColor(mProps.get("textOffColor").toString()));

            textOnColor = Color.parseColor(mProps.get("textOnColor").toString());
            textOffColor = Color.parseColor(mProps.get("textOffColor").toString());
        }
    }
}
