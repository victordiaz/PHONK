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

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;

import org.mozilla.javascript.NativeArray;

import java.util.HashMap;
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

        if (initProps != null && !initProps.containsKey("textSize")) {
            props.put("textSize", AndroidUtils.spToPixels(appRunner.getAppContext(), 6));
        }

        init();
        super.init(data, mCreateCallback, mUpdateCallback);
    }

    private void init() {
        mCreateCallback = r -> {
            Map p = new HashMap<String, Object>();
            p.put("textColor", props.get("textColor"));
            p.put("textSize", props.get("textSize"));
            return new PText(mAppRunner, p);
        };

        mUpdateCallback = r -> {
            PText t = (PText) r.get("view");
            int position = (int) r.get("position");

            t.text((String) data.get(position));
            return null;
        };
    }

    public PTextList add(String text) {
        data.put(data.size(), data, text);
        notifyDataChanged();
        if (mIsAutoScroll) scrollToPosition(data.size() - 1);

        return this;
    }

    public PTextList autoScroll(boolean b) {
        mIsAutoScroll = b;
        return this;
    }

    @Override
    public View textFont(Typeface font) {
        return this;
    }

    @Override
    public View textSize(int size) {
        return this;
    }

    @Override
    public View textColor(String textColor) {
        return this;
    }

    @Override
    public View textColor(int textColor) {
        return this;
    }

    @Override
    public View textSize(float textSize) {
        return this;
    }

    @Override
    public View textStyle(int textStyle) {
        return null;
    }

    @Override
    public View textAlign(int alignment) {
        return null;
    }
}
