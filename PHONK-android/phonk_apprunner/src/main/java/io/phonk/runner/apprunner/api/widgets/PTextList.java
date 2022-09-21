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

import java.util.Map;

import io.phonk.runner.apidoc.annotation.PhonkClass;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.api.common.ReturnInterfaceWithReturn;
import io.phonk.runner.base.utils.AndroidUtils;

@PhonkClass
public class PTextList extends PList implements PTextInterface {
    final int numCol = 1;
    private boolean mIsAutoScroll = false;
    PList list;
    final NativeArray data = new NativeArray(0);
    private ReturnInterfaceWithReturn mCreateCallback;
    private ReturnInterfaceWithReturn mUpdateCallback;

    private float mTextSize = -1;
    private int mTextColor = -1;
    private Typeface mTextfont;

    public PTextList(AppRunner appRunner, Map initProps) {
        super(appRunner, null);

        init();
        super.numColumns(numCol);
        super.init(data, mCreateCallback, mUpdateCallback);
    }

    private void init() {
        mCreateCallback = r -> {
            PText t = new PText(mAppRunner, null);
            t.setTextSize(AndroidUtils.spToPixels(mAppRunner.getAppContext(), 6));
            t.setTextColor(Color.argb(255, 255, 255, 255));

            if (mTextColor != -1)
                t.textColor(mTextColor);
            if (mTextSize != -1)
                t.textSize(mTextSize);

            styler.apply();
            return t;
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

    public PTextList numColumns(int num) {
        list.numColumns(num);
        return this;
    }

    @Override
    public View textFont(Typeface font) {
        mTextfont = font;
        return this;
    }

    @Override
    public View textSize(int size) {
        mTextSize = size;

        return this;
    }

    @Override
    public View textColor(String textColor) {
        mTextColor = Color.parseColor(textColor);

        return this;
    }

    @Override
    public View textColor(int textColor) {
        mTextColor = textColor;

        return this;
    }

    @Override
    public View textSize(float textSize) {
        mTextSize = textSize;

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
