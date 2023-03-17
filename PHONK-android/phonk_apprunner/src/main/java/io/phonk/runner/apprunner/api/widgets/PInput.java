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

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.Map;

import io.phonk.runner.apidoc.annotation.PhonkClass;
import io.phonk.runner.apidoc.annotation.PhonkMethod;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.api.common.ReturnInterface;
import io.phonk.runner.apprunner.api.common.ReturnObject;

@PhonkClass
public class PInput extends androidx.appcompat.widget.AppCompatEditText implements PViewMethodsInterface,
        PTextInterface {
    public final PropertiesProxy props = new PropertiesProxy();
    public final Styler styler;
    private final AppRunner mAppRunner;
    private final EditText mInput;

    public PInput(AppRunner appRunner, Map initProps) {
        super(appRunner.getAppContext());

        mAppRunner = appRunner;
        setClickable(true);
        setFocusableInTouchMode(true);

        styler = new Styler(appRunner, this, props);
        props.onChange((name, value) -> {
            WidgetHelper.applyViewParam(name, value, props, this, appRunner);
            styler.apply(name, value);
            apply(name, value);
        });

        props.eventOnChange = false;
        props.put("textAlign", "left");
        props.put("background", "#00FFFFFF"); // appRunner.pUi.theme.get("secondaryShade"));
        props.put("backgroundPressed", appRunner.pUi.theme.get("secondaryShade"));
        props.put("borderColor", appRunner.pUi.theme.get("secondaryShade"));
        props.put("borderWidth", appRunner.pUtil.dpToPixels(1));
        props.put("padding", appRunner.pUtil.dpToPixels(0));
        props.put("paddingLeft", appRunner.pUtil.dpToPixels(10));
        props.put("paddingRight", appRunner.pUtil.dpToPixels(10));
        props.put("textColor", appRunner.pUi.theme.get("textPrimary"));
        props.put("hintColor", appRunner.pUi.theme.get("secondaryShade"));
        WidgetHelper.fromTo(initProps, props);
        props.eventOnChange = true;
        props.change();

        mInput = this;
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

    @PhonkMethod
    public PInput hint(String hint) {
        props.put("hint", hint);
        return this;
    }

    public PInput text(String txt) {
        this.setText(txt);
        return this;
    }

    public String text() {
        return this.getText().toString();
    }

    public void onChange(final ReturnInterface callbackfn) {

        if (callbackfn != null) {
            // On focus lost, we need to call the callback function
            mInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    ReturnObject r = new ReturnObject(PInput.this);
                    r.put("text", mInput.getText().toString());
                    callbackfn.event(r);
                }
            });

        }
    }

    public void onFocusLost(final ReturnInterface callbackfn) {
        mInput.setOnFocusChangeListener((v, hasFocus) -> {
            ReturnObject r = new ReturnObject(PInput.this);
            r.put("focused", hasFocus);
            callbackfn.event(r);
        });
    }

    public View multiline(boolean isMultiline) {
        props.put("multiline", isMultiline);
        return this;
    }

    @Override
    public void set(float x, float y, float w, float h) {
        styler.setLayoutProps(x, y, w, h);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        hideKeyboard(this);
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }

    @Override
    public void setProps(Map props) {
        WidgetHelper.setProps(this.props, props);
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) mAppRunner.getAppContext().getSystemService(
                Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public Map getProps() {
        return props;
    }

    private void apply(String name, Object value) {
        if (name == null) {
            apply("hint");
            apply("hintColor");
            apply("multiline");

        } else {
            if (value == null) return;
            switch (name) {
                case "hint":
                    setHint(value.toString());
                    break;

                case "hintColor":
                    setHintTextColor(Color.parseColor(value.toString()));
                    break;

                case "multiline":
                    if (value instanceof Boolean && (Boolean) value) {
                        setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                        setSingleLine(false);
                        setGravity(Gravity.TOP);
                    }
                    break;
            }
        }
    }

    private void apply(String name) {
        apply(name, props.get(name));
    }




}
