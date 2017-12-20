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

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

import io.phonk.runner.apidoc.annotation.ProtoMethod;
import io.phonk.runner.apidoc.annotation.ProtoMethodParam;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.base.utils.MLog;

import java.util.ArrayList;

public class PTouchPad_ extends View {
    private static final String TAG = PTouchPad_.class.getSimpleName();

    private static final String TYPE_PROG = "prog";
    private static final String TYPE_FINGER = "finger";

    private final Styler style;

    // paint
    private final Paint mPaint = new Paint();
    private Canvas mCanvas = new Canvas();
    private Bitmap bitmap; // Cache

    // widget size
    private float mWidth;
    private float mHeight;
    private boolean lastTouch = false;
    private OnTouchAreaListener mOnTouchAreaListener;
    private ArrayList<TouchEvent> mTouches = new ArrayList<>();
    private int mStrokeColor;
    private int mBackgroundColor = 0xFF00FFFF;
    private int mPadsColorStroke = 0x0000FF;
    private int mPadsColorBg = 0x880000FF;

    public PTouchPad_(AppRunner appRunner) {
        super(appRunner.getAppContext());
        // getStyles = new Styler(appRunner, this);
        style = null;

        init();
        MLog.d(TAG, "added");
    }

    public void init() {
        mPaint.setStrokeWidth(1.0f);
        mPaint.setAntiAlias(true);
        // mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        if (isInEditMode()) {
            MLog.d(TAG, "edit mode");
            loadDemoValues();
        }

    }

    public void loadDemoValues() {

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        MLog.d(TAG, w + " " + h + " " + oldw + " " + oldh);
        mWidth = w - 1;
        mHeight = h - 1;

        // create mContext bitmap for caching what was drawn
        if (bitmap != null) {
            bitmap.recycle();
        }

        mCanvas = new Canvas();
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas.setBitmap(bitmap);

        // AndroidUtils.setViewGenericShadow(this, w, h);

        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        MLog.d(TAG, "drawing " + bitmap);

        // saved
        canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()),
                new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), null);

        //clear
        mCanvas.drawColor(0, Mode.CLEAR);

        // background
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mBackgroundColor);
        mCanvas.drawRoundRect(new RectF(0, 0, mWidth, mHeight), 5, 5, mPaint);

        mPaint.setStyle(Paint.Style.STROKE);
        mCanvas.drawColor(mStrokeColor);
        mPaint.setColor(mStrokeColor);

        mCanvas.drawRoundRect(new RectF(0, 0, mWidth, mHeight), 5, 5, mPaint);

        for (int i = 0; i < mTouches.size(); i++) {
            TouchEvent t = mTouches.get(i);
            MLog.d(TAG, "painting " + t.id + " " + t.x + " " + t.y);

            mPaint.setColor(mPadsColorBg);
            mPaint.setStyle(Paint.Style.FILL);

            mCanvas.drawCircle(t.x, t.y, 50, mPaint);

            mPaint.setColor(mPadsColorStroke);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(3);

            mCanvas.drawCircle(t.x, t.y, 50, mPaint);

        }
        mTouches.clear();

        if (lastTouch) {
            lastTouch = false;
            invalidate();
        }

		/*
         * Runnable task = new Runnable() {
		 * 
		 * @Override public void run() { // handler.postDelayed(this, duration);
		 * handler.removeCallbacks(this); rl.remove(this); } };
		 * handler.postDelayed(task, 200);
		 */
    }

    public class TouchEvent {
        public String type;
        public int id;
        public String action;
        public int x;
        public int y;

        public TouchEvent(String type, int pointerId, String string, int x, int y) {
            this.type = type;
            this.id = pointerId;
            this.action = string;
            this.x = x;
            this.y = y;
        }
    }

    public TouchEvent newTouch(int id, int x, int y) {
        TouchEvent t = new TouchEvent(TYPE_PROG, id, "move", x, y);

        return t;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // AndroidUtils.dumpMotionEvent(event);

        int action = event.getAction();
        int actionCode = event.getActionMasked();

        // get positions per finger
        int numPoints = event.getPointerCount();
        for (int i = 0; i < numPoints; i++) {
            int id = event.getPointerId(i);
            TouchEvent o = new TouchEvent(TYPE_FINGER, id, "move", (int) event.getX(i), (int) event.getY(i));
            mTouches.add(o);
        }

        // check finger if down or up
        int p = action >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
        TouchEvent te = mTouches.get(p);
        if (te != null) {
            if (actionCode == MotionEvent.ACTION_POINTER_DOWN) {
                te.action = "down";
            } else if (actionCode == MotionEvent.ACTION_POINTER_UP) {
                te.action = "up";
                if (numPoints == 1) {
                    lastTouch = true;
                }
            }

            // if last finger up clear array
            if (actionCode == MotionEvent.ACTION_UP) {
                lastTouch = true;
            }

            mOnTouchAreaListener.onGenericTouch(mTouches);
        }

        invalidate();

        return true;
    }

    /*
            taV.setTouchAreaListener(new PPadView.OnTouchAreaListener() {
            @Override
            public void onGenericTouch(ArrayList<PPadView.TouchEvent> t) {
                ReturnObject o = new ReturnObject();
                o.put("points", t);
                callbackfn.event(o);
            }
        });
     */

    public void destroy() {
        if (bitmap != null) {
            bitmap.recycle();
        }
    }

    public void onTouch(OnTouchAreaListener l) {
        mOnTouchAreaListener = l;
    }

    public interface OnTouchAreaListener {
        public abstract void onGenericTouch(ArrayList<TouchEvent> t);
    }

    void printSamples(MotionEvent ev) {
        final int historySize = ev.getHistorySize();
        final int pointerCount = ev.getPointerCount();
        for (int h = 0; h < historySize; h++) {
            System.out.printf("At time %d:", ev.getHistoricalEventTime(h));
            for (int p = 0; p < pointerCount; p++) {
                System.out.printf("  pointer %d: (%f,%f)", ev.getPointerId(p), ev.getHistoricalX(p, h),
                        ev.getHistoricalY(p, h));
            }
        }
        System.out.printf("At time %d:", ev.getEventTime());
        for (int p = 0; p < pointerCount; p++) {
            System.out.printf("  pointer %d: (%f,%f)", ev.getPointerId(p), ev.getX(p), ev.getY(p));
        }
    }

    @ProtoMethod(description = "Change the pad color", example = "")
    @ProtoMethodParam(params = {"colorHex"})
    public PTouchPad_ padsColor(String c) {
        mPadsColorStroke = Color.parseColor(c);
        int r = Color.red(mPadsColorStroke);
        int g = Color.green(mPadsColorStroke);
        int b = Color.blue(mPadsColorStroke);

        mPadsColorBg = Color.argb(125, r, g, b);
        return this;
    }

    @ProtoMethod(description = "Change the strokeColor", example = "")
    @ProtoMethodParam(params = {"colorHex"})
    public PTouchPad_ strokeColor(String c) {
        mBackgroundColor = Color.parseColor(c);
        return this;
    }

    @ProtoMethod(description = "Change the background color", example = "")
    @ProtoMethodParam(params = {"colorHex"})
    public PTouchPad_ backgroundColor(String c) {
        mBackgroundColor = Color.parseColor(c);
        return this;
    }

}