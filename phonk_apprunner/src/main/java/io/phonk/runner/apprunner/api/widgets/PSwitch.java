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

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.Switch;

import java.util.Map;

import io.phonk.runner.apidoc.annotation.PhonkClass;
import io.phonk.runner.apidoc.annotation.PhonkMethod;
import io.phonk.runner.apidoc.annotation.PhonkMethodParam;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.api.common.ReturnInterface;
import io.phonk.runner.apprunner.api.common.ReturnObject;

@SuppressLint("NewApi")
@PhonkClass
public class PSwitch extends Switch implements PViewMethodsInterface {
    public StyleProperties props = new StyleProperties();
    public Styler styler;
    private Typeface currentFont;

    public PSwitch(AppRunner appRunner) {
        super(appRunner.getAppContext());
        styler = new Styler(appRunner, this, props);
        styler.apply();
        setGravity(Gravity.CENTER);
    }

    public PSwitch onChange(final ReturnInterface callbackfn) {
        // Add the click callback
        this.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ReturnObject r = new ReturnObject(PSwitch.this);
            r.put("changed", isChecked);
            callbackfn.event(r);
        });

        return this;
    }


    @PhonkMethod(description = "Sets the text color", example = "")
    @PhonkMethodParam(params = {"colorHex"})
    public PSwitch color(String c) {
        this.setTextColor(Color.parseColor(c));

        return this;
    }

    @PhonkMethod(description = "Sets the background color", example = "")
    @PhonkMethodParam(params = {"colorHex"})
    public PSwitch background(String c) {
        this.setBackgroundColor(Color.parseColor(c));
        return this;
    }

    @PhonkMethod(description = "Changes the text to the given text", example = "")
    @PhonkMethodParam(params = {"text"})
    public PSwitch text(String text) {
        this.setText(text);
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

    /*
    public PSwitchView center(String how) {
        this.setTextAlignment(TEXT_ALIGNMENT_TEXT_START);
        this.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        this.setTextAlignment(TEXT_ALIGNMENT_TEXT_END);
        this.setTextAlignment(TEXT_ALIGNMENT);
    }
    */
}
