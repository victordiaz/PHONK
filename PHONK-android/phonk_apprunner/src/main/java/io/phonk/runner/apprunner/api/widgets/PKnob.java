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

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Map;

import io.phonk.runner.apidoc.annotation.PhonkClass;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.api.common.ReturnInterface;
import io.phonk.runner.apprunner.api.common.ReturnObject;
import io.phonk.runner.base.utils.AndroidUtils;
import io.phonk.runner.base.views.CanvasUtils;

@PhonkClass
public class PKnob extends PCustomView {

    private boolean autoTextSize;
    private final DecimalFormat df = new DecimalFormat("#.##");
    private ReturnInterface callbackDrag;
    private ReturnInterface callbackRelease;
    private float firstY;
    private float prevVal = 0;
    private float val = 0;
    private int mHeight;
    private float mappedVal;
    private float unmappedVal;

    private float progressSeparation;
    private float borderWidth;
    private float progressWidth;
    private int borderColor;
    private int progressColor;

    final OnDrawCallback mydraw = new OnDrawCallback() {
        @Override
        public void event(PCanvas c) {
            mHeight = c.height;

            c.clear();
            c.cornerMode(false);

            int diameter = 0;
            if (c.width >= c.height) diameter = c.height;
            else diameter = c.width;
            // int halfdiameter = diameter / 2;

            int posX = c.width / 2;
            int posY = c.height / 2;

            c.noFill();

            c.strokeWidth(borderWidth);
            c.stroke(borderColor);
            c.ellipse(posX, posY, diameter - borderWidth, diameter - borderWidth);

            c.strokeWidth(AndroidUtils.dpToPixels(mAppRunner.getAppContext(), (int) progressWidth));
            c.stroke(progressColor); // styler.sliderBorderColor);

            float d = diameter - borderWidth - progressWidth - progressSeparation;
            c.arc(posX, posY, d, d, 180, unmappedVal, false);

            c.fill(styler.textColor);
            c.noStroke();
            c.typeface("monospace");

            df.setRoundingMode(RoundingMode.DOWN);
            if (autoTextSize) c.textSize((diameter / 5));
            else c.textSize(AndroidUtils.spToPixels(getContext(), (int) styler.textSize));

            c.drawTextCentered("" + df.format(mappedVal));
        }
    };
    private float rangeFrom;
    private float rangeTo;

    public PKnob(AppRunner appRunner, Map initProps) {
        super(appRunner, null);

        draw = mydraw;

        props.onChange((name, value) -> {
            WidgetHelper.applyViewParam(name, value, props, this, appRunner);
            styler.apply(name, value);
            apply(name, value);
        });

        props.eventOnChange = false;
        props.put("knobBorderWidth", appRunner.pUtil.dpToPixels(1));
        props.put("knobProgressWidth", appRunner.pUtil.dpToPixels(2));
        props.put("knobProgressSeparation", appRunner.pUtil.dpToPixels(15));
        props.put("knobBorderColor", appRunner.pUi.theme.get("secondaryShade"));
        props.put("knobProgressColor", appRunner.pUi.theme.get("primary"));
        props.put("autoTextSize", false);
        props.put("decimals", 2);
        props.put("from", 0);
        props.put("to", 360);
        props.put("background", "#00FFFFFF");
        props.put("textColor", appRunner.pUi.theme.get("secondary"));
        props.put("textFont", "monospace");
        props.put("textSize", appRunner.pUtil.dpToPixels(4));
        WidgetHelper.fromTo(initProps, props);
        props.eventOnChange = true;
        props.change();
    }

    public PKnob decimals(int num) {
        props.put("decimals", num);
        return this;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                firstY = y;
                prevVal = val;
                return true;
            case MotionEvent.ACTION_MOVE:
                float delta = y - firstY;
                val = prevVal - delta;
                if (val < 0) val = 0;
                if (val > mHeight) val = mHeight;
                unmappedVal = CanvasUtils.map(val, 0, mHeight, 0, 360);
                mappedVal = CanvasUtils.map(val, 0, mHeight, rangeFrom, rangeTo);

                break;
            case MotionEvent.ACTION_UP:
                executeCallbackRelease();
                break;
            default:
                return false;
        }

        executeCallbackDrag();
        invalidate();

        return true;
    }

    private void executeCallbackRelease() {
        ReturnObject ret = new ReturnObject();
        ret.put("value", mappedVal);
        if (callbackRelease != null) callbackRelease.event(ret);
    }

    private void executeCallbackDrag() {
        ReturnObject ret = new ReturnObject();
        ret.put("value", mappedVal);
        if (callbackDrag != null) callbackDrag.event(ret);
    }

    public PKnob onChange(final ReturnInterface callbackfn) {
        this.callbackDrag = callbackfn;
        return this;
    }

    public PKnob onRelease(final ReturnInterface callbackfn) {
        this.callbackRelease = callbackfn;
        return this;
    }

    public PKnob range(float from, float to) {
        props.put("from", from);
        props.put("to", to);
        return this;
    }

    public PKnob valueAndTriggerEvent(float val) {
        value(val);
        executeCallbackDrag();

        return this;
    }

    public PKnob value(float val) {
        props.put("value", val);
        return this;
    }

    private void apply(String name, Object value) {
        if (name == null) {
            apply("knobProgressSeparation");
            apply("knobBorderWidth");
            apply("knobProgressWidth");
            apply("knobBorderColor");
            apply("knobProgressColor");
            apply("autoTextSize");
            apply("decimals");
            apply("from");
            apply("to");
            apply("value");

        } else {
            if (value == null) return;
            switch (name) {
                case "knobProgressSeparation":
                    progressSeparation = styler.toFloat(value);
                    break;

                case "knobBorderWidth":
                    borderWidth = styler.toFloat(value);
                    break;

                case "knobProgressWidth":
                    progressWidth = styler.toFloat(value);
                    break;

                case "knobBorderColor":
                    borderColor = Color.parseColor(value.toString());
                    break;

                case "knobProgressColor":
                    progressColor = Color.parseColor(value.toString());
                    break;

                case "autoTextSize":
                    if (value instanceof Boolean) {
                        autoTextSize = (Boolean) value;
                    }
                    break;

                case "decimals":
                    if (value instanceof Number) {
                        int num = ((Number) value).intValue();
                        String formatString = "#.##";
                        if (num > 0)
                            formatString = "#." + new String(new char[num]).replace("\0", "#");
                        df.applyPattern(formatString);
                        df.setMinimumFractionDigits(num);
                        df.setMinimumFractionDigits(num);
                    }
                    break;

                case "from":
                    if (value instanceof Number) {
                        rangeFrom = ((Number) value).floatValue();
                    }
                    break;

                case "to":
                    if (value instanceof Number) {
                        rangeTo = ((Number) value).floatValue();
                    }
                    break;

                case "value":
                    if (value instanceof Number) {
                        mappedVal = ((Number) value).floatValue();
                        unmappedVal = CanvasUtils.map(mappedVal, rangeFrom, rangeTo, 0, 360);
                        invalidate();
                    }
                    break;
            }
        }
    }

    private void apply(String name) {
        apply(name, props.get(name));
    }




}
