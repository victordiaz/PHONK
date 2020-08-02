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
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.Map;

import io.phonk.runner.apidoc.annotation.PhonkClass;
import io.phonk.runner.apidoc.annotation.PhonkMethod;
import io.phonk.runner.apidoc.annotation.PhonkMethodParam;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.api.common.ReturnInterface;
import io.phonk.runner.apprunner.api.common.ReturnObject;
import io.phonk.runner.base.utils.MLog;

@PhonkClass
public class PInput extends EditText implements PViewMethodsInterface, PTextInterface {
    private final AppRunner mAppRunner;
    private EditText mInput;

    public StylePropertiesProxy props = new StylePropertiesProxy();
    public InputStyler styler;
    private Typeface mFont;

    public PInput(AppRunner appRunner) {
        super(appRunner.getAppContext());

        mAppRunner = appRunner;
        setClickable(true);
        setFocusableInTouchMode(true);

        styler = new InputStyler(appRunner, this, props);
        props.eventOnChange = false;
        props.put("textAlign", props, "left");
        props.put("background", props, appRunner.pUi.theme.get("secondaryShade"));
        props.put("backgroundPressed", props, appRunner.pUi.theme.get("secondaryShade"));
        props.put("borderColor", props, appRunner.pUi.theme.get("secondary"));
        props.put("borderWidth", props, appRunner.pUtil.dpToPixels(1));
        props.put("padding", props, appRunner.pUtil.dpToPixels(25));
        props.put("textColor", props, appRunner.pUi.theme.get("textPrimary"));
        props.put("hintColor", props, appRunner.pUi.theme.get("secondaryShade"));
        props.put("hint", props, "");
        props.eventOnChange = true;
        styler.apply();

        mInput = this;
        textFont(mFont);
    }

    @PhonkMethod
    public PInput hint(String hint) {
        this.setHint(hint);
        return this;
    }

    public void text(String txt) {
        /*
        String joinedText = "";
        for (int i = 0; i < txt.length; i++) {
            joinedText += txt[i];
        }
        */
        this.setText(txt);
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

    @Override
    public View textFont(Typeface font) {
        mFont = font;

        this.setTypeface(font);
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
        this.setTypeface(null, style);
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

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        hideKeyboard(this);
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) mAppRunner.getAppContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    class InputStyler extends Styler {
        int hintColor;

        InputStyler(AppRunner appRunner, View view, StylePropertiesProxy props) {
            super(appRunner, view, props);
        }

        @Override
        public void apply() {
            super.apply();

            hintColor = Color.parseColor(mProps.get("hintColor").toString());
            setHint(mProps.get("hint").toString());
            setHintTextColor(styler.hintColor);
        }
    }
}
