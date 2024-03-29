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
import android.graphics.Typeface;
import android.text.Html;
import android.text.Spanned;
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
    public final PropertiesProxy props = new PropertiesProxy();

    // the props are transformed / accessed using the styler object
    public final Styler styler;

    private ReturnInterface onPressCallback;
    private ReturnInterface onReleaseCallback;

    public PButton(AppRunner appRunner, Map initProps) {
        super(appRunner.getAppContext());

        styler = new Styler(appRunner, this, props);
        props.onChange((name, value) -> {
            WidgetHelper.applyViewParam(name, value, props, this, appRunner);
            styler.apply(name, value);
            apply(name, value);
        });

        props.eventOnChange = false;
        props.put("text", "");
        props.put("textAlign", "center");
        props.put("textStyle", "bold");
        WidgetHelper.fromTo(initProps, props);
        props.eventOnChange = true;
        props.change();
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
        props.put("text", label);
        return this;
    }

    public String text() {
        return getText().toString();
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
        styler.textFont = font;
        props.put("textFont", "custom");
        MLog.d(TAG, "--> " + "font");

        return this;
    }

    @Override
    public View textSize(int size) {
        return textSize((float) size);
    }

    @Override
    public PButton textColor(String textColor) {
        props.put("textColor", textColor);
        return this;
    }

    @Override
    public PButton textColor(int c) {
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

    public PButton background(String c) {
        props.put("background", c);
        return this;
    }

    @PhonkMethod(description = "Sets html text", example = "")
    @PhonkMethodParam(params = {"htmlText"})
    public PButton html(String htmlText) {
        props.put("text", Html.fromHtml(htmlText));
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
                    if (value instanceof Spanned) {
                        setText((Spanned) value);
                    } else {
                        setText(value.toString());
                    }
                    break;
            }
        }
    }

    private void apply(String name) {
        apply(name, props.get(name));
    }




}
