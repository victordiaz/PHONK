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

@PhonkClass
public class PToggle extends androidx.appcompat.widget.AppCompatToggleButton implements PViewMethodsInterface,
        PTextInterface {

    public final PropertiesProxy props = new PropertiesProxy();
    private final Styler styler;

    private int textOnColor;
    private int textOffColor;
    private int backgroundColor;
    private int backgroundCheckedColor;

    public PToggle(AppRunner appRunner, Map initProps) {
        super(appRunner.getAppContext());

        styler = new Styler(appRunner, this, props);
        props.onChange((name, value) -> {
            WidgetHelper.applyViewParam(name, value, props, this, appRunner);
            styler.apply(name, value);
            apply(name, value);
        });

        props.eventOnChange = false;
        props.put("textColor", appRunner.pUi.theme.get("primary"));
        props.put("background", "#00FFFFFF");
        props.put("backgroundChecked", appRunner.pUi.theme.get("primaryShade"));
        props.put("borderColor", appRunner.pUi.theme.get("secondaryShade"));
        props.put("borderWidth", appRunner.pUtil.dpToPixels(1));
        props.put("checked", false);
        props.put("text", "Toggle");
        props.put("textOff", "OFF");
        props.put("textOffColor", appRunner.pUi.theme.get("textPrimary"));
        props.put("textOn", "ON");
        props.put("textOnColor", appRunner.pUi.theme.get("textPrimary"));
        WidgetHelper.fromTo(initProps, props);
        props.eventOnChange = true;
        props.change();
    }

    @Override
    public PToggle textFont(Typeface font) {
        styler.textFont = font;
        props.put("textFont", "custom");
        return this;
    }

    @Override
    public View textSize(int size) {
        return textSize((float) size);
    }

    @Override
    public View textColor(String textColor) {
        props.put("textColor", textColor);
        return this;
    }

    @Override
    public View textColor(int c) {
        styler.textColor = c;
        props.put("textColor", "custom");
        return this;
    }

    @Override
    public View textSize(float textSize) {
        props.put("textSize", textSize);
        return this;
    }

    @Override
    public View textStyle(int style) {
        styler.textStyle = style;
        props.put("textStyle", "custom");
        return this;
    }

    @Override
    public View textAlign(int alignment) {
        styler.textAlign = alignment;
        props.put("textAlign", "custom");
        return this;
    }

    public PToggle onChange(final ReturnInterface callbackfn) {
        // Add change listener
        this.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {
                setTextColor(textOnColor);
                styler.mBackgroundDrawable.setBackground(backgroundCheckedColor);
            } else {
                setTextColor(textOffColor);
                styler.mBackgroundDrawable.setBackground(backgroundColor);
            }

            ReturnObject r = new ReturnObject(PToggle.this);
            r.put("checked", isChecked);
            callbackfn.event(r);
        });

        return this;
    }

    public PToggle text(String label) {
        props.put("text", label);
        props.put("textOn", label);
        props.put("textOff", label);
        return this;
    }

    public String text() {
        return getText().toString();
    }

    public PToggle textOn(String label) {
        props.put("textOn", label);
        return this;
    }

    public String textOn() {
        return getTextOn().toString();
    }

    public PToggle textOff(String label) {
        props.put("textOff", label);
        return this;
    }

    public String textOff() {
        return getTextOff().toString();
    }

    public PToggle checked(boolean b) {
        props.put("checked", b);
        return this;
    }

    public boolean checked() {
        return isChecked();
    }

    @Override
    public void set(float x, float y, float w, float h) {
        styler.setLayoutProps(x, y, w, h);
    }

    @Override
    public void setProps(Map props) {
        WidgetHelper.setProps(this.props, props);
    }

    @Override
    public Map getProps() {
        return props;
    }

    private void apply(String name, Object value) {
        if (name == null) {
            apply("background");
            apply("backgroundChecked");
            apply("checked");
            apply("text");
            apply("textOff");
            apply("textOffColor");
            apply("textOn");
            apply("textOnColor");

        } else {
            if (value == null) return;
            switch (name) {
                case "background":
                    backgroundColor = Color.parseColor(value.toString());
                    if (!isChecked()) styler.mBackgroundDrawable.setBackground(backgroundColor);
                    break;

                case "backgroundChecked":
                    backgroundCheckedColor = Color.parseColor(value.toString());
                    if (isChecked()) styler.mBackgroundDrawable.setBackground(backgroundCheckedColor);
                    break;

                case "checked":
                    setChecked(value instanceof Boolean ? (Boolean) value : false);
                    break;

                case "text":
                    setText(value.toString());
                    break;

                case "textOff":
                    setTextOff(value.toString());
                    break;

                case "textOffColor":
                    textOffColor = Color.parseColor(value.toString());
                    if (!isChecked()) setTextColor(textOffColor);
                    break;

                case "textOn":
                    setTextOn(value.toString());
                    break;

                case "textOnColor":
                    textOnColor = Color.parseColor(value.toString());
                    if (isChecked()) setTextColor(textOnColor);
                    break;
            }
        }
    }

    private void apply(String name) {
        apply(name, props.get(name));
    }


}
