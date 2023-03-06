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
import android.text.Html;
import android.view.MotionEvent;
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
public class PButton extends androidx.appcompat.widget.AppCompatButton implements PViewMethodsInterface,
        PTextInterface {
    private static final String TAG = PButton.class.getSimpleName();

    // this is a props proxy for the user
    public final StylePropertiesProxy props = new StylePropertiesProxy();

    // the props are transformed / accessed using the styler object
    public final Styler styler;

    private Typeface mFont;
    private int mStyle;

    private ReturnInterface onPressCallback;
    private ReturnInterface onReleaseCallback;

    public PButton(AppRunner appRunner, Map initProps) {
        super(appRunner.getAppContext());

        styler = new Styler(appRunner, this, props);
        props.eventOnChange = false;
        props.put("textStyle", props, "bold");
        props.put("textAlign", props, "center");
        // props.put("srcTintPressed", props, appRunner.pUi.theme.get("colorSecondary"));
        Styler.fromTo(initProps, props);
        props.eventOnChange = true;
        styler.apply();

        setText("");
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean isEventAlreadyHandled = super.onTouchEvent(event);
        boolean hasPressCallback = onPressCallback != null;
        boolean hasReleaseCallback = onReleaseCallback != null;
        boolean isEventHandled = false;
        final ReturnObject r = new ReturnObject(PButton.this);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (hasPressCallback) {
                    onPressCallback.event(r);
                    isEventHandled = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                // should be considerer as ACTION_UP according to https://developer.android.com/reference/android/view/MotionEvent#ACTION_CANCEL
            case MotionEvent.ACTION_CANCEL:
                if (hasReleaseCallback) {
                    onReleaseCallback.event(r);
                    isEventHandled = true;
                }
                break;
        }
        return isEventAlreadyHandled || isEventHandled || hasReleaseCallback;
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

    @PhonkMethod(description = "Triggers the function when the button is pressed", example = "")
    @PhonkMethodParam(params = {"function"})
    public PButton onPress(final ReturnInterface onPressCallback) {
        this.onPressCallback = onPressCallback;
        return this;
    }

    @PhonkMethod(description = "Triggers the function when the button is released", example = "")
    @PhonkMethodParam(params = {"function"})
    public PButton onRelease(final ReturnInterface onReleaseCallback) {
        this.onReleaseCallback = onReleaseCallback;
        return this;
    }

    @PhonkMethod(description = "Triggers the function when the button is pressed for a long time", example = "")
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

    @Override
    public PButton textFont(Typeface font) {
        mFont = font;
        this.setTypeface(font, mStyle);
        MLog.d(TAG, "--> " + "font");

        return this;
    }

    @Override
    public View textSize(int size) {
        setTextSize(size);
        return this;
    }

    @Override
    public PButton textColor(String c) {
        this.setTextColor(Color.parseColor(c));
        return this;
    }

    @Override
    public PButton textColor(int c) {
        this.setTextColor(c);
        return this;
    }

    @Override
    public View textSize(float size) {
        this.setTextSize(size);
        return this;
    }

    @Override
    public View textStyle(int style) {
        mStyle = style;
        this.setTypeface(mFont, style);
        return this;
    }

    @Override
    public View textAlign(int alignment) {
        this.setGravity(alignment);
        return this;
    }

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

    @PhonkMethod(description = "Button position", example = "")
    @PhonkMethodParam(params = {"x", "y"})
    public PButton pos(int x, int y) {
        this.setX(x);
        this.setY(y);

        return this;
    }

    @Override
    public void set(float x, float y, float w, float h) {
        styler.setLayoutProps(x, y, w, h);
    }

    public PAnimation anim() {
        return new PAnimation(this);
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
