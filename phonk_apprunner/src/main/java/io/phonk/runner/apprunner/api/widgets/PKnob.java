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

import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Map;

import io.phonk.runner.apprunner.api.common.ReturnInterface;
import io.phonk.runner.apprunner.api.common.ReturnObject;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.StyleProperties;
import io.phonk.runner.base.utils.AndroidUtils;
import io.phonk.runner.base.views.CanvasUtils;

public class PKnob extends PCustomView implements PViewMethodsInterface, PTextInterface {

    private static final String TAG = PKnob.class.getSimpleName();

    public StyleProperties props = new StyleProperties();
    public Styler styler;

    private Typeface textStyle = Typeface.DEFAULT;
    private Typeface textFont;

    private ArrayList touches;
    private float firstY;
    private float prevVal = 0;
    private float val = 0;
    private boolean autoTextSize = false;
    private ReturnInterface callback;
    private int mWidth;
    private int mHeight;
    private float mappedVal;
    private float unmappedVal;
    private float rangeFrom = 0;
    private float rangeTo = 360;

    public PKnob(AppRunner appRunner) {
        super(appRunner);

        draw = mydraw;
        styler = new Styler(appRunner, this, props);
        styler.apply();

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
        public void event(PCanvasM c) {
            mWidth = c.width;
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

            c.strokeWidth(styler.knobBorderWidth);
            c.stroke(styler.knobBorderColor);
            c.ellipse(posX, posY, diameter - styler.knobBorderWidth, diameter - styler.knobBorderWidth);

            c.strokeWidth(30); //styler.sliderBorderSize);
            c.stroke(styler.knobProgressColor); // styler.sliderBorderColor);

            float d = diameter - styler.knobBorderWidth - styler.knobProgressWidth - styler.knobProgressSeparation;
            c.arc(posX, posY, d, d, 180, unmappedVal, false);

            /*
            c.noFill();
            c.strokeWidth(5);
            c.stroke("#000000");

            Path p1 = c.path();

            int diameter = 0;
            if (c.width >= c.height) diameter = c.height;
            else diameter = c.width;
            int halfdiameter = diameter / 2;

            // mAppRunner.pUtil.
            float val = CanvasUtils.map(y, 0, c.height, 0, 360);
            p1.addArc(new RectF(0, 0, diameter, diameter), 180, val);
            p1.lineTo(halfdiameter, halfdiameter);
            // c.drawPath(p1);
            Path p2 = c.path();
            float r = diameter / 5;
            p2.addCircle(halfdiameter, halfdiameter, r, Path.Direction.CCW);

            c.mCanvasBuffer.save();
            c.mCanvasBuffer.clipPath(p2, Region.Op.DIFFERENCE);
            c.drawPath(p1);
            c.mCanvasBuffer.restore();
            */

            c.fill(styler.textColor);
            c.noStroke();
            c.setTypeface("monospace");

            if (autoTextSize) c.textSize((diameter / 5));
            else c.textSize(AndroidUtils.spToPixels(getContext(), (int) styler.textSize));

            c.drawTextCentered("" + Math.round(mappedVal * 100.0f) / 100.0f);
        }
    };

    public PKnob onChange(final ReturnInterface callbackfn) {
        this.callback = callbackfn;

        return this;
    }

    public PKnob range(float from, float to) {
        rangeFrom = from;
        rangeTo = to;

        return this;
    }

    public void value(float val) {
        this.mappedVal = val;
        this.unmappedVal = CanvasUtils.map(val, rangeFrom, rangeTo, 0, 360);

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
    public void setStyle(Map style) {
        styler.setStyle(style);
    }

    @Override
    public Map getStyle() {
        return props;
    }


    @Override
    public View font(Typeface font) {
        this.textFont = font;

        return this;
    }

    @Override
    public View textSize(int size) {
        return null;
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
        return null;
    }

    @Override
    public View textStyle(int textStyle) {
       // this.textStyle = textStyle;
        return this;
    }

    @Override
    public View textAlign(int alignment) {
        return this;
    }
}
