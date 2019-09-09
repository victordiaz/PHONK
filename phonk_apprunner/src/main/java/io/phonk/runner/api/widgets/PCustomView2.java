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

package io.phonk.runner.api.widgets;

import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;

import io.phonk.runner.api.other.PLooper;
import io.phonk.runner.apidoc.annotation.ProtoField;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.base.utils.MLog;


public class PCustomView2 extends View {

    private static final String TAG = PCustomView2.class.getSimpleName();

    protected final AppRunner mAppRunner;
    private PCanvasM mPCanvas;

    @ProtoField(description = "Time interval between draws", example = "")
    private int drawInterval = 35;
    protected boolean mAutoDraw = false;
    private PLooper loop;

    public interface OnSetupCallback { void event(PCustomView2 c); }
    public interface OnDrawCallback { void event(PCanvasM c); }

    public OnSetupCallback setup;
    public OnDrawCallback draw;

    public PCustomView2(AppRunner appRunner) {
        super(appRunner.getAppContext());
        mAppRunner = appRunner;
        this.setBackgroundColor(Color.TRANSPARENT);
        // this.setZOrderOnTop(true); //necessary
        // getHolder().setFormat(PixelFormat.TRANSPARENT);
        // prepareLooper();
        init();
    }

    private void init() {
        // mTransparentPaint = new Paint();
        // mTransparentPaint.setStyle(Paint.Style.FILL);
        // mTransparentPaint.setColor(Color.BLUE);
        mPCanvas = new PCanvasM(mAppRunner);
    }

    /*
    private void prepareLooper() {
        loop = mAppRunner.pUtil.loop(drawInterval, new PLooper.LooperCB() {
            @Override
            public void event() {
                if (draw != null) {
                    draw.event(mPCanvas);
                    invalidate();
                }
            }
        });
        loop.speed(drawInterval);
    }

    private void startLooper() {
        loop.start();
        mAutoDraw = true;
    }

    public void drawInterval(int ms) {
        loop.speed(ms);
    }

    public void post_init() {
        mPCanvas = new Canvas();

        if (setup != null) setup.event(PCustomView.this);
        // startLooper();
    }
    */

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        MLog.d(TAG, "new size --> " + w + " " + h);
        mPCanvas.prepare(w, h);

        /*
        if (!absoluteMode) {
            width = w;
            height = h;
            post_init();
            invalidate();
        }
        */
    }

    public void onDraw(OnDrawCallback callback) {
        MLog.d(TAG, "setting up callback;");
        draw = callback;
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        MLog.d(TAG, "onDraw");

        /*
        mCanvasBuffer.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), mTransparentPaint);
        canvas.drawBitmap(mTransparentBmp,
                new Rect(0,0, mTransparentBmp.getWidth(), mTransparentBmp.getHeight()),
                new Rect(0,0, mTransparentBmp.getWidth(), mTransparentBmp.getHeight()), null);

        */

        mPCanvas.setCanvas(canvas);
        draw.event(mPCanvas);
        mPCanvas.drawAll();
        // if (!mAutoDraw && draw != null) draw.event(mPCanvas);
        // layers.drawAll(canvas);
    }

    /*
    @ProtoMethod(description = "For each change in the canvas it will redraw it self. Have in mind that mainly to try out things as is not very fast.", example = "")
    @ProtoMethodParam(params = {"boolean"})
    public PCustomView autoDraw(boolean b) {
        mAutoDraw = b;
        loop.start();
        return this;
    }
    */

    /**
     * refresh the canvas after painting if the flag is true
     */
    /*
    private PCustomView refresh() {
        if (mAutoDraw) invalidate();
        return this;
    }
    */
}
