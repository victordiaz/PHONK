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
    public final PropertiesProxy props = new PropertiesProxy();
    public final Styler styler;

    public PText(AppRunner appRunner, Map initProps) {
        super(appRunner.getAppContext());

        styler = new Styler(appRunner, this, props);
        props.onChange((name, value) -> {
            WidgetHelper.applyViewParam(name, value, props, this, appRunner);
            styler.apply(name, value);
            apply(name, value);
        });

        props.eventOnChange = false;
        props.put("background", "#00FFFFFF");
        props.put("text", "");
        props.put("textAlign", "left");
        props.put("textColor", appRunner.pUi.theme.get("textPrimary"));
        WidgetHelper.fromTo(initProps, props);
        props.eventOnChange = true;
        props.change();
    }

    public PText color(String c) {
        props.put("textColor", c);
        return this;
    }

    public PText background(String c) {
        props.put("background", c);
        return this;
    }

    @PhonkMethod(description = "Enables/disables the scroll in the text view", example = "")
    @PhonkMethodParam(params = {"size"})
    public PText scrollable(boolean b) {
        props.put("scrollable", b);
        return this;
    }

    public PText text(String text) {
        props.put("text", text);
        return this;
    }

    public PText text(String... txt) {
        StringBuilder joinedText = new StringBuilder();
        for (String s : txt) {
            joinedText.append(" ").append(s);
        }
        return text(joinedText.toString());
    }

    public String text() {
        return getText().toString();
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
        props.put("text", text);
        return this;
    }

    @PhonkMethod(description = "Appends text to the text view", example = "")
    @PhonkMethodParam(params = {"text"})
    public PText append(String text) {
        return text(getText() + text);
    }

    @PhonkMethod(description = "Clears the text", example = "")
    public PText clear() {
        return text("");
    }

    @PhonkMethod(description = "Changes the box size of the text", example = "")
    @PhonkMethodParam(params = {"w", "h"})
    public PText boxsize(int w, int h) {
        props.put("w", w);
        props.put("h", h);
        return this;
    }

    @PhonkMethod(description = "Fits the text to the bounding box", example = "")
    @PhonkMethodParam(params = {"w", "h"})
    public PText autoFitText(boolean b) {
        props.put("autoFit", b);
        return this;
    }

    @PhonkMethod(description = "Sets a new position for the text", example = "")
    @PhonkMethodParam(params = {"x", "y"})
    public PText pos(int x, int y) {
        props.put("x", x);
        props.put("y", y);
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
        styler.textAlign = Gravity.CENTER_VERTICAL;
        props.put("textAlign", "custom");
        return this;
    }

    public PText textFont(Typeface font) {
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

    private void apply(String name, Object value) {
        if (name == null) {
            apply("autoFit");
            apply("scrollable");
            apply("text");

        } else {
            if (value == null) return;
            switch (name) {
                case "autoFit":
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;
                    if (value instanceof Boolean) {
                        TextViewCompat.setAutoSizeTextTypeWithDefaults(this, (Boolean) value ? AUTO_SIZE_TEXT_TYPE_UNIFORM : AUTO_SIZE_TEXT_TYPE_NONE);
                    }
                    break;

                case "scrollable":
                    if (value instanceof Boolean) {
                        if ((Boolean) value) {
                            setMovementMethod(new ScrollingMovementMethod());
                            setVerticalScrollBarEnabled(true);
                        } else {
                            setMovementMethod(null);
                        }
                    }
                    break;

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
