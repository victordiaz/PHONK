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

import java.util.Map;

import io.phonk.runner.apprunner.api.common.ReturnInterface;
import io.phonk.runner.apprunner.api.common.ReturnObject;
import io.phonk.runner.apprunner.api.other.PLooper;
import io.phonk.runner.apprunner.api.other.PhonkNativeArray;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.StyleProperties;
import io.phonk.runner.base.utils.MLog;
import io.phonk.runner.base.views.CanvasUtils;

public class PTouchPad extends PCustomView implements PViewMethodsInterface {

    private static final String TAG = PTouchPad.class.getSimpleName();

    public StyleProperties props = new StyleProperties();
    public Styler styler;
    private PhonkNativeArray touches;
    private ReturnInterface onTouchCallback;
    private int mWidth;
    private int mHeight;
    private float rangeXFrom = 0;
    private float rangeXTo = 1.0f;
    private float rangeYFrom = 0;
    private float rangeYTo = 1.0f;

    public PTouchPad(AppRunner appRunner) {
        super(appRunner);

        draw = mydraw;
        styler = new Styler(appRunner, this, props);
        styler.apply();

        appRunner.pUi.onTouches(this, new ReturnInterface() {
            @Override
            public void event(ReturnObject r) {
            // remap values
            touches = (PhonkNativeArray) r.get("touches");
            PhonkNativeArray remmapedTouches = new PhonkNativeArray(touches.size());
            if (touches != null) {

                for (int i = 0; i < touches.size(); i++) {
                    ReturnObject t = (ReturnObject) touches.get(i);
                    float unmappedXVal = (float) t.get("x");
                    float unmappedYVal = (float) t.get("y");
                    float mappedXVal = CanvasUtils.map(unmappedXVal, 0, mWidth, rangeXFrom, rangeXTo);
                    float mappedYVal = CanvasUtils.map(unmappedYVal, 0, mHeight, rangeYFrom, rangeYTo);

                    if (mappedXVal < rangeXFrom) mappedXVal = rangeXFrom;
                    if (mappedXVal > rangeXTo) mappedXVal = rangeXTo;

                    if (mappedYVal < rangeYFrom) mappedYVal = rangeYFrom;
                    if (mappedYVal > rangeXTo) mappedYVal = rangeYTo;

                    ReturnObject tRemapped = new ReturnObject();
                    tRemapped.put("x", mappedXVal);
                    tRemapped.put("y", mappedYVal);
                    tRemapped.put("action", t.get("action"));
                    remmapedTouches.addPE(i, tRemapped);
                }
            }
            ReturnObject ro = new ReturnObject();
            ro.put("touches", remmapedTouches);
            ro.put("count", touches.size());
            onTouchCallback.event(ro);

            // trigger animation
            if (touches.size() > 0 && !looper.isLooping()) {
                MLog.d(TAG, "start touch");
                looper.start();
            // if the last pointer is up then stop animation
            } else if (touches.size() == 1) {
                ReturnObject t = ((ReturnObject) touches.get(0));
                if (t.get("action") == "up") {
                    MLog.d(TAG, "stop touch");
                    touches = null;
                    invalidate();
                    looper.stop();
                }
            }
            // invalidate();
            }
        });
    }

    public PTouchPad onTouch(ReturnInterface callback) {
        onTouchCallback = callback;

        return this;
    }

    PLooper looper = new PLooper(mAppRunner, 5, new PLooper.LooperCB() {
        @Override
        public void event() {
            invalidate();
            // MLog.d("touches", "size " + touches.size());
        }
    });

    private void executeCallback() {
        if (onTouchCallback != null) {

        }
    }

    OnDrawCallback mydraw = new OnDrawCallback() {
        @Override
        public void event(PCanvasM c) {
            mWidth = c.width;
            mHeight = c.height;

            c.clear();
            c.cornerMode(false);

            if (touches != null) {
                for (int i = 0; i < touches.size(); i++) {
                    ReturnObject r = (ReturnObject) touches.get(i);
                    float unmappedXVal = (float) r.get("x");
                    float unmappedYVal = (float) r.get("y");

                    if (unmappedXVal < 0) unmappedXVal = 0;
                    if (unmappedXVal > c.width) unmappedXVal = c.width;
                    if (unmappedYVal < 0) unmappedYVal = 0;
                    if (unmappedYVal > c.height) unmappedYVal = c.height;

                    c.fill(styler.padColor);
                    c.stroke(styler.padBorderColor);
                    c.strokeWidth(styler.padBorderSize);
                    c.ellipse(unmappedXVal, unmappedYVal, styler.padSize, styler.padSize);
                }
            }
        }
    };

    public PTouchPad range(float fromX, float toX, float fromY, float toY) {
        rangeXFrom = fromX;
        rangeXTo = toX;
        rangeYFrom = fromY;
        rangeYTo = toY;

        return this;
    }

    public void value(int index, String action, float valX, float valY) {
        float mappedXVal = valX;
        float mappedYVal = valY;

        float unmappedXVal = CanvasUtils.map(valX, rangeXFrom, rangeXTo, 0, mWidth);
        float unmappedYVal = CanvasUtils.map(valY, rangeYFrom, rangeYTo, 0, mHeight);

        executeCallback();
        this.invalidate();
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

}
