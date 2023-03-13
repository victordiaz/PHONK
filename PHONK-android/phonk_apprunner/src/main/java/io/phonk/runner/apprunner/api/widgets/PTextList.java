/*
 * Part of Phonk http://www.phonk.io
 * A prototyping platform for Android devices
 *
 * Copyright (C) 2013 - 2017 Victor Diaz Barrales @victordiaz (Protocoder)
 * Copyright (C) 2017 - Victor Diaz Barrales @victordiaz (Phonk)
 *
 * Phonk is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Phonk is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Phonk. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package io.phonk.runner.apprunner.api.widgets;

import android.graphics.Typeface;
import android.view.View;

import org.mozilla.javascript.NativeArray;

import java.util.Map;

import io.phonk.runner.apidoc.annotation.PhonkClass;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.api.common.ReturnInterfaceWithReturn;
import io.phonk.runner.base.utils.AndroidUtils;

@PhonkClass
public class PTextList extends PList implements PTextInterface {
    final NativeArray data = new NativeArray(0);
    private boolean mIsAutoScroll = false;
    private ReturnInterfaceWithReturn mCreateCallback;
    private ReturnInterfaceWithReturn mUpdateCallback;

    public PTextList(AppRunner appRunner, Map initProps) {
        super(appRunner, initProps);

        props.onChange((name, value) -> {
            WidgetHelper.applyViewParam(name, value, props, this, appRunner);
            styler.apply(name, value);
            apply(name, value);
        });

        if (initProps != null && !initProps.containsKey("textSize")) {
            props.put("textSize", AndroidUtils.spToPixels(appRunner.getAppContext(), 6));
        }

        init();
        super.init(data, mCreateCallback, mUpdateCallback);
    }

    private void init() {
        mCreateCallback = r -> {
            PText t = new PText(mAppRunner, null);
            t.props.put("textColor", props.get("textColor"));
            t.props.put("textSize", props.get("textSize"));
            return t;
        };
        mUpdateCallback = r -> ((PText) r.get("view")).props.put("text", data.get((int) r.get("position")));
    }

    public PTextList add(String text) {
        data.put(data.size(), data, text);
        notifyDataChanged();
        if (mIsAutoScroll) scrollToPosition(data.size() - 1);

        return this;
    }

    public PTextList autoScroll(boolean b) {
        props.put("autoScroll", b);
        return this;
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
    public View textColor(int textColor) {
        styler.textColor = textColor;
        props.put("textColor", "custom");
        return this;
    }

    @Override
    public View textSize(float textSize) {
        props.put("textSize", textSize);
        return this;
    }

    @Override
    public View textStyle(int textStyle) {
        styler.textStyle = textStyle;
        props.put("textStyle", "custom");
        return this;
    }

    @Override
    public View textAlign(int alignment) {
        styler.textAlign = alignment;
        props.put("textAlign", "custom");
        return this;
    }

    private void apply(String name, Object value) {
        if (name == null) {
            apply("autoScroll");

        } else {
            if (value == null) return;
            switch (name) {
                case "autoScroll":
                    if (value instanceof Boolean) {
                        mIsAutoScroll = (Boolean) value;
                    }
                    break;
            }
        }
    }

    private void apply(String name) {
        apply(name, props.get(name));
    }
}
