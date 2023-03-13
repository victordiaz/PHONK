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

import androidx.appcompat.widget.SwitchCompat;

import java.util.Map;

import io.phonk.runner.apidoc.annotation.PhonkClass;
import io.phonk.runner.apidoc.annotation.PhonkMethod;
import io.phonk.runner.apidoc.annotation.PhonkMethodParam;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.api.common.ReturnInterface;
import io.phonk.runner.apprunner.api.common.ReturnObject;

@SuppressLint("NewApi")
@PhonkClass
public class PSwitch extends SwitchCompat implements PViewMethodsInterface {
    public final PropertiesProxy props = new PropertiesProxy();
    public final Styler styler;

    public PSwitch(AppRunner appRunner) {
        super(appRunner.getAppContext());
        styler = new Styler(appRunner, this, props);
        props.onChange((name, value) -> {
            WidgetHelper.applyViewParam(name, value, props, this, appRunner);
            styler.apply(name, value);
            apply(name, value);
        });

        props.put("textAlign", "center");
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


    public PSwitch color(String c) {
        props.put("textColor", c);
        return this;
    }

    public PSwitch background(String c) {
        props.put("background", c);
        return this;
    }

    public PSwitch text(String text) {
        props.put("text", text);
        return this;
    }

    public String text() {
        return getText().toString();
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
            apply("text");

        } else {
            if (value == null) return;
            switch (name) {
                case "text":
                    setText(value.toString());
                    break;
            }
        }
    }

    private void apply(String name) {
        apply(name, props.get(name));
    }

}
