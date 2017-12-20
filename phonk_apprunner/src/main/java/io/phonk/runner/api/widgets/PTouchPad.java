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

package io.phonk.runner.api.widgets;

import io.phonk.runner.api.common.ReturnInterface;
import io.phonk.runner.api.common.ReturnObject;
import io.phonk.runner.api.other.PLooper;
import io.phonk.runner.api.other.ProtocoderNativeArray;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.StyleProperties;
import io.phonk.runner.base.utils.MLog;

import java.util.Map;

/**
 * Created by biquillo on 11/09/16.
 */
public class PTouchPad extends PCanvas implements PViewMethodsInterface {

    private static final String TAG = PTouchPad.class.getSimpleName();

    public StyleProperties props = new StyleProperties();
    public Styler styler;
    private ProtocoderNativeArray touches;
    private ReturnInterface onTouch;

    public PTouchPad(AppRunner appRunner) {
        super(appRunner);
        MLog.d(TAG, "create touchpad");

        draw = mydraw;
        styler = new Styler(appRunner, this, props);
        styler.apply();

        appRunner.pUi.onTouches2(this, new ReturnInterface() {
            @Override
            public void event(ReturnObject r) {

                onTouch.event(r);
                touches = (ProtocoderNativeArray) r.get("touches");

                // MLog.d("touches", "size " + touches.size());

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
        onTouch = callback;

        return this;
    }

    PLooper looper = new PLooper(mAppRunner, 5, new PLooper.LooperCB() {
        @Override
        public void event() {
            invalidate();
            // MLog.d("touches", "size " + touches.size());
        }
    });

    OnDrawCallback mydraw = new OnDrawCallback() {
        @Override
        public void event(PCanvas c) {
            c.clear();
            c.mode(false);

            if (touches != null) {
                for (int i = 0; i < touches.size(); i++) {
                    ReturnObject r = (ReturnObject) touches.get(i);
                    float x = (float) r.get("x");
                    float y = (float) r.get("y");

                    if (true) {
                        if (x < 0) x = 0;
                        if (x > width) x = width;
                        if (y < 0) y = 0;
                        if (y > height) y = height;
                    }

                    c.fill(styler.padColor);
                    c.stroke(styler.padBorderColor);
                    c.strokeWidth(styler.padBorderSize);
                    c.ellipse(x, y, styler.padSize, styler.padSize);
                }
            }
        }
    };

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
