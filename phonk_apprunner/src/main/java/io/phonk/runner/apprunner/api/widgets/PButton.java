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
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.BounceInterpolator;

import java.util.Map;

import io.phonk.runner.apidoc.annotation.PhonkClass;
import io.phonk.runner.apidoc.annotation.PhonkMethod;
import io.phonk.runner.apidoc.annotation.PhonkMethodParam;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.api.common.ReturnInterface;
import io.phonk.runner.apprunner.api.common.ReturnObject;
import io.phonk.runner.base.utils.MLog;

@PhonkClass
public class PButton extends androidx.appcompat.widget.AppCompatButton implements PViewMethodsInterface, PTextInterface {
    private static final String TAG = PButton.class.getSimpleName();
    private final AppRunner mAppRunner;

    // this is a props proxy for the user
    public StylePropertiesProxy props = new StylePropertiesProxy();

    // the props are transformed / accessed using the styler object
    public ButtonStyler styler;

    private Typeface mFont;

    public PButton(AppRunner appRunner, Map initProps) {
        super(appRunner.getAppContext());
        mAppRunner = appRunner;

        styler = new ButtonStyler(appRunner, this, props);
        props.eventOnChange = false;
        props.put("textStyle", props, "bold");
        props.put("textAlign", props, "center");
        // props.put("srcTintPressed", props, appRunner.pUi.theme.get("colorSecondary"));
        props.put("text", props, "");
        styler.fromTo(initProps, props);
        props.eventOnChange = true;
        styler.apply();

        setTypeface(mFont);
    }

    public PButton text(String label) {
        setText(label);
        return this;
    }

    @PhonkMethod(description = "Triggers the function when the button is clicked", example = "")
    @PhonkMethodParam(params = {"function"})
    public PButton onClick(final ReturnInterface callbackfn) {
        // Set on click behavior
        this.setOnClickListener(v -> {
            if (callbackfn != null) {

                ReturnObject r = new ReturnObject(PButton.this);
                r.put("action", "clicked");
                callbackfn.event(r);
            }
        });

        return this;
    }

    @PhonkMethod(description = "Triggers the function when the button is released", example = "")
    @PhonkMethodParam(params = {"function"})
    public PButton onPress(final ReturnInterface callbackfn) {
        // Set on click behavior
        this.setOnTouchListener((v, event) -> {
            ReturnObject r = new ReturnObject(PButton.this);
            r.put("action", "release");

            if (event.getAction() == MotionEvent.ACTION_DOWN && callbackfn != null) {
                callbackfn.event(r);
            } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                MLog.d("qqq", "qq");
            }

            return false;
        });

        return this;
    }

    @PhonkMethod(description = "Triggers the function when the button is released", example = "")
    @PhonkMethodParam(params = {"function"})
    public PButton onLongPress(final ReturnInterface callbackfn) {
        // Set on click behavior
        this.setOnLongClickListener(v -> {
            if (callbackfn != null) {

                ReturnObject r = new ReturnObject(PButton.this);
                r.put("action", "longPress");
                callbackfn.event(r);
            }
            return true;
        });

        return this;
    }

    @PhonkMethod(description = "Changes the font type to the button", example = "")
    @PhonkMethodParam(params = {"Typeface"})
    public PButton textFont(Typeface f) {
        mFont = f;
        this.setTypeface(f);
        MLog.d(TAG, "--> " + "font");

        return this;
    }

    @Override
    public View textStyle(int style) {
        this.setTypeface(mFont, style);
        return this;
    }

    @Override
    public View textAlign(int alignment) {
        this.setGravity(alignment);
        return this;
    }

    @Override
    public View textSize(int size) {
        this.textSize(size);
        return this;
    }

    @Override
    @PhonkMethod(description = "Changes the font text color", example = "")
    @PhonkMethodParam(params = {"colorHex"})
    public PButton textColor(String c) {
        this.setTextColor(Color.parseColor(c));
        return this;
    }

    @Override
    @PhonkMethod(description = "Changes the font text color", example = "")
    @PhonkMethodParam(params = {"colorHex"})
    public PButton textColor(int c) {
        this.setTextColor(c);
        return this;
    }

    @PhonkMethod(description = "Changes the background color", example = "")
    @PhonkMethodParam(params = {"colorHex"})
    public PButton background(String c) {
        this.setBackgroundColor(Color.parseColor(c));
        return this;
    }

    @PhonkMethod(description = "Sets html text", example = "")
    @PhonkMethodParam(params = {"htmlText"})
    public PButton html(String htmlText) {
        this.setText(Html.fromHtml(htmlText));

        return this;
    }

    @PhonkMethod(description = "Changes the button size", example = "")
    @PhonkMethodParam(params = {"w", "h"})
    public PButton boxsize(int w, int h) {
        this.setWidth(w);
        this.setHeight(h);

        return this;
    }

    @Override
    @PhonkMethod(description = "Changes the text size", example = "")
    @PhonkMethodParam(params = {"size"})
    public View textSize(float size) {
        this.setTextSize(size);

        return this;
    }

    @PhonkMethod(description = "Button position", example = "")
    @PhonkMethodParam(params = {"x", "y"})
    public PButton pos(int x, int y) {
        this.setX(x);
        this.setY(y);

        return this;
    }

    @Override
    public void set(float x, float y, float w, float h) { styler.setLayoutProps(x, y, w, h); }

    @Override
    public void setProps(Map style) {
        styler.setProps(style);
    }

    @Override
    public Map getProps() {
        return props;
    }

    public PAnimation anim() {
        return new PAnimation(this);
    }

    class ButtonStyler extends Styler {
        ButtonStyler(AppRunner appRunner, View view, StylePropertiesProxy props) {
            super(appRunner, view, props);
        }

        @Override
        public void apply() {
            super.apply();

            text(mProps.get("text").toString());
        }
    }
}
