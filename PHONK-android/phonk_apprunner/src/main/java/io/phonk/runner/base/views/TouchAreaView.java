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

package io.phonk.runner.base.views;

/*
 * use vectors
 * add values to it
 *
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

public class TouchAreaView extends View {
    private static final String TAG = "TouchAreaView";
    // paint
    private final Paint mPaint = new Paint();
    private Canvas mCanvas = new Canvas();
    private Bitmap bitmap; // Cache

    // widget size
    private float mWidth;
    private float mHeight;
    private boolean touching = false;
    private OnTouchAreaListener mOnTouchAreaListener;
    private boolean showArea;
    private Rect mCleanRect;

    public TouchAreaView(Context context, boolean showArea) {
        super(context);

        this.showArea = showArea;
        init();
    }

    public void init() {
        mPaint.setStrokeWidth(1.0f);
        // mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        if (isInEditMode()) {
            loadDemoValues();
        }
    }

    public void loadDemoValues() {

    }

    public TouchAreaView(Context context) {
        super(context);
        init();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Convert coordinates to our internal coordinate system
        float xPointer = event.getX();
        float yPointer = event.getY();
        // MLog.d(TAG, xPointer + " " + yPointer);

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_CANCEL:

            case MotionEvent.ACTION_UP:
                touching = false;
                break;

            case MotionEvent.ACTION_DOWN:
                touching = true;
                break;

            case MotionEvent.ACTION_MOVE:
                touching = xPointer > 0 && xPointer < mWidth && yPointer > 0 && yPointer < mHeight;
                break;

        }

        mOnTouchAreaListener.onTouch(this, touching, xPointer, yPointer);

        invalidate();
        // return touching;
        return super.onTouchEvent(event);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w - 1;
        mHeight = h - 1;

        mCleanRect = new Rect(0, 0, (int) mWidth, (int) mHeight);

        // create mContext bitmap for caching what was drawn
        if (bitmap != null) {
            bitmap.recycle();
        }

        mCanvas = new Canvas();
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas.setBitmap(bitmap);

        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        synchronized (this) {

            // saved
            canvas.drawBitmap(bitmap, mCleanRect, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), null);

            if (showArea) {
                mPaint.setColor(0x88FFFFFF);
                mPaint.setStyle(Style.STROKE);

                mCanvas.drawRect(mCleanRect, mPaint);

                if (touching) {
                    mPaint.setStyle(Paint.Style.FILL);
                    mPaint.setColor(0x880000FF);
                } else {
                    mPaint.setStyle(Paint.Style.STROKE);
                    mCanvas.drawColor(0, Mode.CLEAR);
                    mPaint.setColor(0x88000000);
                }
                mCanvas.drawRect(mCleanRect, mPaint);
            }
        }
    }

    public void destroy() {
        if (bitmap != null) {
            bitmap.recycle();
        }
    }

    public void setTouchAreaListener(OnTouchAreaListener l) {
        mOnTouchAreaListener = l;
    }

    void printSamples(MotionEvent ev) {
        final int historySize = ev.getHistorySize();
        final int pointerCount = ev.getPointerCount();
        for (int h = 0; h < historySize; h++) {
            System.out.printf("At time %d:", ev.getHistoricalEventTime(h));
            for (int p = 0; p < pointerCount; p++) {
                System.out.printf("  pointer %d: (%f,%f)",
                        ev.getPointerId(p),
                        ev.getHistoricalX(p, h),
                        ev.getHistoricalY(p, h)
                );
            }
        }
        System.out.printf("At time %d:", ev.getEventTime());
        for (int p = 0; p < pointerCount; p++) {
            System.out.printf("  pointer %d: (%f,%f)", ev.getPointerId(p), ev.getX(p), ev.getY(p));
        }
    }

    public interface OnTouchAreaListener {
        void onTouch(TouchAreaView touchAreaView, boolean touching, float xPointer, float yPointer);
    }

    public static class TouchEvent {
        final String type;
        final int num;
        final int action;
        final int x;
        final int y;

        public TouchEvent(String type, int pointerId, int action2, int x, int y) {
            this.type = type;
            this.num = pointerId;
            this.action = action2;
            this.x = x;
            this.y = y;
        }

    }

}