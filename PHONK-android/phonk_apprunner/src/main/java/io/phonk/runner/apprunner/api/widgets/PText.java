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
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;

import androidx.core.widget.TextViewCompat;

import java.util.Map;

import io.phonk.runner.apidoc.annotation.PhonkClass;
import io.phonk.runner.apidoc.annotation.PhonkMethod;
import io.phonk.runner.apidoc.annotation.PhonkMethodParam;
import io.phonk.runner.apprunner.AppRunner;

@PhonkClass
public class PText extends androidx.appcompat.widget.AppCompatTextView implements PViewMethodsInterface,
        PTextInterface {
    public final StylePropertiesProxy props = new StylePropertiesProxy();
    public final TextStyler styler;
    private Typeface currentFont;

    public PText(AppRunner appRunner, Map initProps) {
        super(appRunner.getAppContext());

        styler = new TextStyler(appRunner, this, props);
        props.eventOnChange = false;
        props.put("background", props, "#00FFFFFF");
        props.put("textColor", props, appRunner.pUi.theme.get("textPrimary"));
        // textStyle.put("textSize", textStyle, 12);
        props.put("textAlign", props, "left");
        Styler.fromTo(initProps, props);
        props.eventOnChange = true;
        styler.apply();
    }

    @PhonkMethod(description = "Sets the text color", example = "")
    @PhonkMethodParam(params = {"colorHex"})
    public PText color(String c) {
        this.setTextColor(Color.parseColor(c));

        return this;
    }

    @PhonkMethod(description = "Sets the background color", example = "")
    @PhonkMethodParam(params = {"colorHex"})
    public PText background(String c) {
        this.setBackgroundColor(Color.parseColor(c));
        return this;
    }

    @PhonkMethod(description = "Enables/disables the scroll in the text view", example = "")
    @PhonkMethodParam(params = {"size"})
    public PText scrollable(boolean b) {
        if (b) {
            this.setMovementMethod(new ScrollingMovementMethod());
            this.setVerticalScrollBarEnabled(true);
            // this.setGravity(Gravity.BOTTOM);
        } else {
            this.setMovementMethod(null);
        }
        return this;
    }

    @PhonkMethod(description = "Changes the text to the given text", example = "")
    @PhonkMethodParam(params = {"text"})
    public PText text(String text) {
        this.setText(text);
        return this;
    }

    @PhonkMethod(description = "Changes the text to the given text", example = "")
    @PhonkMethodParam(params = {"text, text, ..., text"})
    public PText text(String... txt) {
        StringBuilder joinedText = new StringBuilder();
        for (String s : txt) {
            joinedText.append(" ").append(s);
        }
        this.setText(joinedText.toString());

        return this;
    }


    @PhonkMethod(description = "Changes the text to the given html text", example = "")
    @PhonkMethodParam(params = {"htmlText"})
    public PText html(String html) {

        Spanned text;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            text = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            text = Html.fromHtml(html);
        }
        this.setText(text);

        return this;
    }

    @PhonkMethod(description = "Appends text to the text view", example = "")
    @PhonkMethodParam(params = {"text"})
    public PText append(String text) {
        this.setText(getText() + text);

        return this;
    }

    @PhonkMethod(description = "Clears the text", example = "")
    public PText clear() {
        this.setText("");
        return this;
    }

    @PhonkMethod(description = "Changes the box size of the text", example = "")
    @PhonkMethodParam(params = {"w", "h"})
    public PText boxsize(int w, int h) {
        this.setWidth(w);
        this.setHeight(h);
        return this;
    }

    @PhonkMethod(description = "Fits the text to the bounding box", example = "")
    @PhonkMethodParam(params = {"w", "h"})
    public PText autoFitText(boolean b) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return this;

        if (b) TextViewCompat.setAutoSizeTextTypeWithDefaults(this, AUTO_SIZE_TEXT_TYPE_UNIFORM);
        else TextViewCompat.setAutoSizeTextTypeWithDefaults(this, AUTO_SIZE_TEXT_TYPE_NONE);
        return this;
    }

    @PhonkMethod(description = "Sets a new position for the text", example = "")
    @PhonkMethodParam(params = {"x", "y"})
    public PText pos(int x, int y) {
        this.setX(x);
        this.setY(y);
        return this;
    }

    @PhonkMethod(description = "Specifies a shadow for the text", example = "")
    @PhonkMethodParam(params = {"x", "y", "radius", "colorHex"})
    public PText shadow(int x, int y, int r, String c) {
        this.setShadowLayer(r, x, y, Color.parseColor(c));
        return this;
    }

    @PhonkMethod(description = "Centers the text inside the textview", example = "")
    @PhonkMethodParam(params = {"Typeface"})
    public PText center(String centering) {
        this.setGravity(Gravity.CENTER_VERTICAL);
        return this;
    }

    @PhonkMethod(description = "Changes the font", example = "")
    @PhonkMethodParam(params = {"Typeface"})
    public PText textFont(Typeface f) {
        this.currentFont = f;
        this.setTypeface(f);
        return this;
    }

    @PhonkMethod(description = "Sets the text size", example = "")
    @PhonkMethodParam(params = {"size"})
    public PText textSize(int size) {
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
        this.setTypeface(currentFont, style);
        return this;
    }

    @Override
    public View textAlign(int alignment) {
        this.setGravity(alignment);
        return this;
    }

    @Override
    public void set(float x, float y, float w, float h) {
        styler.setLayoutProps(x, y, w, h);
    }

    static class TextStyler extends Styler {
        TextStyler(AppRunner appRunner, View view, StylePropertiesProxy props) {
            super(appRunner, view, props);
        }

        @Override
        public void apply() {
            super.apply();
        }
    }    @Override
    public void setProps(Map style) {
        styler.setProps(style);
    }

    @Override
    public Map getProps() {
        return props;
    }



}
