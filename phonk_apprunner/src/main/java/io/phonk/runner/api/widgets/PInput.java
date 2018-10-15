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

package io.phonk.runner.api.widgets;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import io.phonk.runner.api.common.ReturnInterface;
import io.phonk.runner.api.common.ReturnObject;
import io.phonk.runner.apidoc.annotation.ProtoMethod;
import io.phonk.runner.apidoc.annotation.ProtoMethodParam;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.StyleProperties;

import java.util.Map;

public class PInput extends androidx.appcompat.widget.AppCompatEditText implements PViewMethodsInterface, PTextInterface {

    private final AppRunner mAppRunner;
    private EditText mInput;

    public StyleProperties props = new StyleProperties();
    public Styler styler;

    public PInput(AppRunner appRunner) {
        super(appRunner.getAppContext());

        mAppRunner = appRunner;
        setClickable(true);
        setFocusableInTouchMode(true);

        styler = new Styler(appRunner, this, props);
        styler.apply();

        setHintTextColor(styler.hintColor);

        mInput = this;
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
        mInput.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                ReturnObject r = new ReturnObject(PInput.this);
                r.put("focused", hasFocus);
                callbackfn.event(r);
            }
        });
    }


    @Override
    public View font(Typeface font) {
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
    @ProtoMethod(description = "Changes the font text color", example = "")
    @ProtoMethodParam(params = {"colorHex"})
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
    public void setStyle(Map style) {
        styler.setStyle(style);
    }

    @Override
    public Map getStyle() {
        return props;
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        hideKeyboard(this);
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager) mAppRunner.getAppContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
