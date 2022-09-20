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

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.math.MathUtils;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Map;

import io.phonk.runner.apidoc.annotation.PhonkClass;
import io.phonk.runner.apidoc.annotation.PhonkMethod;
import io.phonk.runner.apidoc.annotation.PhonkMethodParam;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.api.common.ReturnInterface;
import io.phonk.runner.apprunner.api.common.ReturnObject;
import io.phonk.runner.base.utils.AndroidUtils;
import io.phonk.runner.base.views.CanvasUtils;

@SuppressLint("ViewConstructor")
@PhonkClass
public class PSlider extends PCustomView implements PViewMethodsInterface {
    private static final String TAG = PSlider.class.getSimpleName();

    public final StylePropertiesProxy props = new StylePropertiesProxy();
    public SliderStyler styler;

    private ReturnInterface callbackDrag;
    private ReturnInterface callbackRelease;

    private float currentValue = 0;
    private float rangeFrom = 0;
    private float rangeTo = 1;

    private String mode = "direct";
    private float initialXValue;
    private float initialYValue;
    private float initialValueWhenTouched = 0;
    private float userValue = 0;
    private boolean isVertical = false;
    private String text;
    private final DecimalFormat df = new DecimalFormat("#.##");
    private boolean firstDraw = true;

    public PSlider(final AppRunner appRunner, final Map<String, Object> initProps) {
        super(appRunner, initProps);

        setupDrawCallback();
        setupStyle(appRunner, initProps);
        decimals(2);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mode.equals("drag")) {
                    initialXValue = event.getX();
                    initialYValue = event.getY();
                    initialValueWhenTouched = currentValue;
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                if (isVertical) {
                    handleVerticalMovement(event);
                } else {
                    handleHorizontalMovement(event);
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

    @PhonkMethod(description = "Sets the decimal count", example = "")
    @PhonkMethodParam(params = "decimal count")
    public PSlider decimals(int num) {
        String formatString = "#";
        if (num > 0) formatString = "#." + new String(new char[num]).replace("\0", "#");
        df.applyPattern(formatString);
        df.setMinimumFractionDigits(num);
        df.setMaximumFractionDigits(num);
        return this;
    }

    @PhonkMethod(description = "onChange callback", example = "")
    @PhonkMethodParam(params = "callback")
    public PSlider onChange(final ReturnInterface callbackfn) {
        this.callbackDrag = callbackfn;
        return this;
    }

    @PhonkMethod(description = "onDrag callback", example = "")
    @PhonkMethodParam(params = "callback")
    public PSlider onDrag(final ReturnInterface callbackfn) {
        this.callbackDrag = callbackfn;
        return this;
    }

    @PhonkMethod(description = "onRelease callback", example = "")
    @PhonkMethodParam(params = "callback")
    public PSlider onRelease(final ReturnInterface callbackfn) {
        this.callbackRelease = callbackfn;
        return this;
    }

    @PhonkMethod(description = "Sets slider range", example = "")
    @PhonkMethodParam(params = "from and to are floats")
    public PSlider range(float from, float to) {
        rangeFrom = from;
        rangeTo = to;
        return this;
    }

    @PhonkMethod(description = "Sets the init value", example = "")
    @PhonkMethodParam(params = "float")
    public PSlider value(final float userValue) {
        this.userValue = userValue;
        final int maxCanvasValue = computeMaxCanvasValue();
        this.currentValue = CanvasUtils.map(userValue, rangeFrom, rangeTo, 0, maxCanvasValue);
        this.invalidate();
        return this;
    }

    @PhonkMethod(description = "Sets the init value and fire onDrag()", example = "")
    @PhonkMethodParam(params = "float")
    public PSlider valueAndTriggerEvent(float val) {
        this.value(val);
        executeCallbackDrag();
        return this;
    }

    @PhonkMethod(description = "Sets the slider mode", example = "")
    @PhonkMethodParam(params = "direct or drag, try them both!")
    public PSlider mode(String mode) {
        this.mode = mode;
        return this;
    }

    @PhonkMethod(description = "Sets the slider orientation", example = "")
    @PhonkMethodParam(params = "boolean")
    public PSlider verticalMode(final boolean isVertical) {
        this.isVertical = isVertical;
        return this;
    }

    @PhonkMethod(description = "Sets a text inside the slider", example = "")
    @PhonkMethodParam(params = "string")
    public PSlider text(final String text) {
        this.text = text;
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
    public Map<String, Object> getProps() {
        return props;
    }

    private void handleHorizontalMovement(final MotionEvent event) {
        if (mode.equals("direct")) {
            currentValue = MathUtils.clamp(event.getX(), 0, canvasWidth);
        } else if (mode.equals("drag")) {
            final float delta = event.getX() - initialXValue;
            currentValue = MathUtils.clamp(initialValueWhenTouched + delta, 0, canvasWidth);
        }
    }

    private void handleVerticalMovement(final MotionEvent event) {
        if (mode.equals("direct")) {
            currentValue = canvasHeight - MathUtils.clamp(event.getY(), 0, canvasHeight);
        } else if (mode.equals("drag")) {
            final float delta = initialYValue - event.getY();
            currentValue = MathUtils.clamp(initialValueWhenTouched + delta, 0, canvasHeight);
        }
    }

    private float computeCurrentValueForTheUser() {
        final int maxCanvasValue = computeMaxCanvasValue();
        return CanvasUtils.map(currentValue, 0, maxCanvasValue, rangeFrom, rangeTo);
    }

    private void executeCallbackDrag() {
        ReturnObject ret = new ReturnObject();
        ret.put("value", computeCurrentValueForTheUser());
        if (callbackDrag != null) callbackDrag.event(ret);
    }

    private void executeCallbackRelease() {
        ReturnObject ret = new ReturnObject();
        ret.put("value", computeCurrentValueForTheUser());
        if (callbackRelease != null) callbackRelease.event(ret);
    }

    private int computeMaxCanvasValue() {
        return isVertical ? canvasHeight : canvasWidth;
    }

    private void setupDrawCallback() {
        draw = canvas -> {
            canvasWidth = canvas.width;
            canvasHeight = canvas.height;

            canvas.clear();
            canvas.cornerMode(true);

            canvas.fill(styler.slider);

            canvas.strokeWidth((float) styler.borderWidth);
            canvas.stroke(styler.borderColor);

            if (firstDraw) {
                // the value is not correctly set the first time as the canvas is not created yet, so set it now
                this.value(userValue);
                firstDraw = false;
            }
            if (isVertical) {
                canvas.rect(0, canvas.height - currentValue, canvas.width, currentValue, (float) styler.borderRadius, (float) styler.borderRadius);
            } else {
                canvas.rect(0, 0, currentValue, canvas.height, (float) styler.borderRadius, (float) styler.borderRadius);
            }

            canvas.porterDuff("XOR");
            df.setRoundingMode(RoundingMode.DOWN);

            canvas.fill(styler.textColor);
            canvas.noStroke();
            canvas.typeface("monospace");

            canvas.textSize(AndroidUtils.spToPixels(getContext(), (int) styler.textSize));
            final String textToDisplay = text != null ? text : df.format(computeCurrentValueForTheUser());
            canvas.drawTextCentered("" + textToDisplay);
        };
    }

    private void setupStyle(final AppRunner appRunner, final Map<String, Object> initProps) {
        styler = new SliderStyler(appRunner, this, props);
        props.eventOnChange = false;
        props.put("slider", null, appRunner.pUi.theme.get("primary"));
        props.put("sliderPressed", null, appRunner.pUi.theme.get("primary"));
        props.put("sliderHeight", null, 20);
        props.put("sliderBorderSize", null, 0);
        props.put("sliderBorderColor", null, "#00FFFFFF");
        Styler.fromTo(initProps, props);
        props.eventOnChange = true;
        styler.apply();
    }

    static class SliderStyler extends Styler {
        int slider;
        float sliderHeight;

        SliderStyler(AppRunner appRunner, View view, StylePropertiesProxy props) {
            super(appRunner, view, props);
        }

        @Override
        public void apply() {
            super.apply();

            slider = Color.parseColor((String) mProps.get("slider"));
            sliderHeight = toFloat(mProps.get("sliderHeight"));
        }
    }
}
