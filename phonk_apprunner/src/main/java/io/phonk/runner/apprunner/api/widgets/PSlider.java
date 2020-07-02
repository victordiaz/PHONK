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

import android.view.MotionEvent;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import io.phonk.runner.apidoc.annotation.PhonkClass;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.api.common.ReturnInterface;
import io.phonk.runner.apprunner.api.common.ReturnObject;
import io.phonk.runner.base.utils.AndroidUtils;
import io.phonk.runner.base.views.CanvasUtils;

@PhonkClass
public class PSlider extends PCustomView implements PViewMethodsInterface {
    private static final String TAG = PSlider.class.getSimpleName();

    public StyleProperties props = new StyleProperties();
    public Styler styler;

    private ArrayList touches;
    private float x;
    private float y;
    private boolean touching;
    private ReturnInterface callback;
    private int mWidth;
    private int mHeight;
    private float unmappedVal;
    private float mappedVal;
    private float rangeFrom = 0;
    private float rangeTo = 1;

    private DecimalFormat df;
    private String formatString = "#.##";
    private int numDecimals = 1;

    public PSlider(AppRunner appRunner) {
        super(appRunner);

        draw = mydraw;
        styler = new Styler(appRunner, this, props);
        styler.apply();

        DecimalFormatSymbols symbol = new DecimalFormatSymbols(Locale.FRANCE);
        symbol.setDecimalSeparator('.');
        df = new DecimalFormat(formatString, symbol);
        df.setMinimumFractionDigits(2);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        x = event.getX();
        y = event.getY();

        if (x < 0) x = 0;
        if (x > mWidth) x = mWidth;
        if (y < 0) y = 0;
        if (y > mHeight) y = mHeight;

        unmappedVal = x;
        mappedVal = CanvasUtils.map(x, 0, mWidth, rangeFrom, rangeTo);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                return false;
        }

        executeCallback();
        invalidate();

        return true;
    }

    private void executeCallback() {
        if (callback != null) {
            ReturnObject ret = new ReturnObject();
            ret.put("value", mappedVal);
            callback.event(ret);
        }
    }

    OnDrawCallback mydraw = new OnDrawCallback() {
        @Override
        public void event(PCanvas c) {
            mWidth = c.width;
            mHeight = c.height;

            c.clear();
            c.cornerMode(true);

            if (!touching) c.fill(styler.slider);
            else c.fill(styler.sliderPressed);
            c.strokeWidth(styler.sliderBorderSize);
            c.stroke(styler.sliderBorderColor);
            c.rect(0, 0, unmappedVal, c.height);

            df.setRoundingMode(RoundingMode.DOWN);

            c.fill(styler.textColor);
            c.noStroke();
            c.typeface("monospace");

            c.textSize(AndroidUtils.spToPixels(getContext(), (int) styler.textSize));
            c.drawTextCentered("" + df.format(mappedVal));
        }
    };

    public PSlider onChange(final ReturnInterface callbackfn) {
        this.callback = callbackfn;
        return this;
    }

    public PSlider range(float from, float to) {
        rangeFrom = from;
        rangeTo = to;

        return this;
    }

    public void value(float val) {
        this.mappedVal = val;
        this.unmappedVal = CanvasUtils.map(mappedVal, rangeFrom, rangeTo, 0, mWidth);

        this.invalidate();
    }

    public void valueAndTriggerEvent(float val) {
        this.value(val);
        executeCallback();
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

}
