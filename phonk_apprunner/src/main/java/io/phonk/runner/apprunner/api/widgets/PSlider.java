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
import android.view.MotionEvent;
import android.view.View;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;

import io.phonk.runner.apidoc.annotation.PhonkClass;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.api.common.ReturnInterface;
import io.phonk.runner.apprunner.api.common.ReturnObject;
import io.phonk.runner.base.utils.AndroidUtils;
import io.phonk.runner.base.utils.MLog;
import io.phonk.runner.base.views.CanvasUtils;

@PhonkClass
public class PSlider extends PCustomView implements PViewMethodsInterface {
    private static final String TAG = PSlider.class.getSimpleName();

    public StylePropertiesProxy props = new StylePropertiesProxy();
    public SliderStyler styler;

    private ReturnInterface callbackDrag;
    private ReturnInterface callbackRelease;

    private ArrayList touches;
    private float x;
    private float y;
    private boolean touching;
    private float unmappedVal;
    private float mappedVal;
    private float rangeFrom = 0;
    private float rangeTo = 1;

    private String mode = "direct";
    private float firstX;
    private float prevVal = 0;
    private float val = 0;

    private DecimalFormat df;

    public PSlider(AppRunner appRunner, Map initProps) {
        super(appRunner, initProps);

        draw = mydraw;

        styler = new SliderStyler(appRunner, this, props);
        props.eventOnChange = false;
        props.put("slider", props, (String) appRunner.pUi.theme.get("primary"));
        props.put("sliderPressed", props, (String) appRunner.pUi.theme.get("primary"));
        props.put("sliderHeight", props, 20);
        props.put("sliderBorderSize", props, 0);
        props.put("sliderBorderColor", props, "#00FFFFFF");
        styler.fromTo(initProps, props);
        props.eventOnChange = true;
        styler.apply();

        df = new DecimalFormat("#.##");
        decimals(2);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        x = event.getX();
        y = event.getY();


        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                firstX = x;
                prevVal = val;
                return true;
            case MotionEvent.ACTION_MOVE:
                if (mode.equals("direct")) {
                    if (x < 0) x = 0;
                    if (x > mWidth) x = mWidth;
                    if (y < 0) y = 0;
                    if (y > mHeight) y = mHeight;
                    unmappedVal = x;
                    mappedVal = CanvasUtils.map(x, 0, mWidth, rangeFrom, rangeTo);
                } else if (mode.equals("drag")) {
                    float delta = x - firstX;
                    val = prevVal + delta;
                    if (val < 0) val = 0;
                    if (val > mWidth) val = mWidth;
                    unmappedVal = val; // CanvasUtils.map(val, 0, mWidth, 0, 360);
                    mappedVal = CanvasUtils.map(val, 0, mWidth, rangeFrom, rangeTo);
                }
                executeCallbackDrag();

                break;

            case MotionEvent.ACTION_UP:
                executeCallbackRelease();
                break;

            default:
                return false;
        }

        invalidate();

        return true;
    }

    private void executeCallbackDrag() {
        ReturnObject ret = new ReturnObject();
        ret.put("value", mappedVal);
        if (callbackDrag != null) callbackDrag.event(ret);
    }

    private void executeCallbackRelease() {
        ReturnObject ret = new ReturnObject();
        ret.put("value", mappedVal);
        if (callbackRelease != null) callbackRelease.event(ret);
    }

    OnDrawCallback mydraw = new OnDrawCallback() {
        @Override
        public void event(PCanvas c) {
            mWidth = c.width;
            mHeight = c.height;

            PSlider.this.unmappedVal = CanvasUtils.map(mappedVal, rangeFrom, rangeTo, 0, mWidth);

            c.clear();
            c.cornerMode(true);

            if (!touching) c.fill(styler.slider);
            else c.fill(styler.sliderPressed);

            c.strokeWidth((float) styler.borderWidth);
            c.stroke(styler.borderColor);


            c.rect(0, 0, unmappedVal, c.height, (float) styler.borderRadius, (float) styler.borderRadius);

            c.porterDuff("XOR");
            df.setRoundingMode(RoundingMode.DOWN);

            c.fill(styler.textColor);
            c.noStroke();
            c.typeface("monospace");

            c.textSize(AndroidUtils.spToPixels(getContext(), (int) styler.textSize));
            c.drawTextCentered("" + df.format(mappedVal));
        }
    };

    public PSlider decimals(int num) {
        String formatString = "#";
        if (num > 0) formatString = "#." + new String(new char[num]).replace("\0", "#");
        df.applyPattern(formatString);
        df.setMinimumFractionDigits(num);
        df.setMaximumFractionDigits(num);
        return this;
    }

    public PSlider onChange(final ReturnInterface callbackfn) {
        this.callbackDrag = callbackfn;
        return this;
    }

    public PSlider onDrag(final ReturnInterface callbackfn) {
        this.callbackDrag = callbackfn;
        return this;
    }

    public PSlider onRelease(final ReturnInterface callbackfn) {
        this.callbackRelease = callbackfn;
        return this;
    }

    public PSlider range(float from, float to) {
        rangeFrom = from;
        rangeTo = to;

        return this;
    }

    public PSlider value(float val) {
        this.mappedVal = val;
        this.invalidate();

        return this;
    }

    public PSlider valueAndTriggerEvent(float val) {
        this.value(val);
        executeCallbackDrag();

        return this;
    }

    public PSlider mode(String mode) {
        this.mode = mode;

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


    class SliderStyler extends Styler {
        int slider;
        int sliderPressed;
        float sliderHeight;
        float sliderBorderSize;
        int sliderBorderColor;

        SliderStyler(AppRunner appRunner, View view, StylePropertiesProxy props) {
            super(appRunner, view, props);
        }

        @Override
        public void apply() {
            super.apply();

            slider = Color.parseColor(mProps.get("slider").toString());
            sliderPressed = Color.parseColor(mProps.get("sliderPressed").toString());
            sliderHeight = toFloat(mProps.get("sliderHeight"));
        }
    }

}
