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
public class PCheckBox extends androidx.appcompat.widget.AppCompatCheckBox implements PViewMethodsInterface,
        PTextInterface {
    public final PropertiesProxy props = new PropertiesProxy();
    public final Styler styler;

    private Typeface mFont;
    private int mStyle;

    public PCheckBox(AppRunner appRunner) {
        super(appRunner.getAppContext());
        styler = new Styler(appRunner, this, props);
    }

    public PCheckBox onChange(final ReturnInterface callbackfn) {

        // Add the click callback
        this.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ReturnObject r = new ReturnObject(PCheckBox.this);
            r.put("checked", isChecked);
            callbackfn.event(r);
        });

        return this;
    }

    @Override
    public View textFont(Typeface font) {
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

}
