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

import android.graphics.Canvas;
import android.view.View;

import java.util.Map;

import io.phonk.runner.apidoc.annotation.PhonkClass;
import io.phonk.runner.apidoc.annotation.PhonkField;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.base.utils.MLog;

@PhonkClass
public class PCustomView extends View implements PViewMethodsInterface {
    private static final String TAG = PCustomView.class.getSimpleName();
    // this is a props proxy for the user
    public final PropertiesProxy props = new PropertiesProxy();
    // the props are transformed / accessed using the styler object
    public final Styler styler;
    protected final AppRunner mAppRunner;
    @PhonkField(description = "Time interval between draws", example = "")
    public OnSetupCallback setup;
    public OnDrawCallback draw;
    protected int canvasWidth;
    protected int canvasHeight;
    private PCanvas mPCanvas;

    public PCustomView(AppRunner appRunner, Map initProps) {
        super(appRunner.getAppContext());
        mAppRunner = appRunner;

        styler = new Styler(appRunner, this, props);
        props.onChange((name, value) -> {
            WidgetHelper.applyViewParam(name, value, props, this, mAppRunner);
            styler.apply(name, value);
        });

        props.eventOnChange = false;
        props.put("background", "#00FFFFFF");
        WidgetHelper.fromTo(initProps, props);
        props.eventOnChange = true;
        props.change();

        mPCanvas = new PCanvas(appRunner);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        MLog.d(TAG, "new size --> " + w + " " + h);
        mPCanvas.prepare(w, h);

    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        MLog.d(TAG, "onDraw");

        mPCanvas.setCanvas(canvas);
        draw.event(mPCanvas);
        mPCanvas.drawAll();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        invalidate();
    }

    public void onDraw(OnDrawCallback callback) {
        MLog.d(TAG, "setting up callback;");
        draw = callback;
    }

    @Override
    public void set(float x, float y, float w, float h) {
        styler.setLayoutProps(x, y, w, h);
    }

    public interface OnSetupCallback {
        void event(PCustomView c);
    }


    public interface OnDrawCallback {
        void event(PCanvas c);
    }

    @Override
    public void setProps(Map props) {
        WidgetHelper.setProps(this.props, props);
    }

    @Override
    public Map getProps() {
        return props;
    }
}
