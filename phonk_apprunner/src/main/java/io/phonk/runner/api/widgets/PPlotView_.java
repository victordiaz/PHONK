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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import io.phonk.runner.apidoc.annotation.ProtoMethod;
import io.phonk.runner.apidoc.annotation.ProtoMethodParam;
import io.phonk.runner.base.utils.MLog;
import io.phonk.runner.base.views.CanvasUtils;

public class PPlotView_ extends View {

    private static final String TAG = PPlotView_.class.getSimpleName();

    // paint
    private final Paint mPaint = new Paint();
    private Canvas mCanvas = new Canvas();

    //store each plot values
    HashMap<String, Plot> plots = new HashMap<String, Plot>();

    // util data
    private float mLastX;
    private float mMaxValue;
    private float mMinValue;

    // widget size
    private float mWidth;
    private float mHeight;
    private int mNumPoints;
    private final int mCurrentPosition = 0;
    private int mDefinition = 1;
    private float mMinBoundary;
    private float mMaxBoundary;
    private boolean mReady = false;
    private float thickness = 2;

    public PPlotView_(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public PPlotView_(Context context) {
        super(context);
        init();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public void init() {
        mDefinition = 5;

        mPaint.setStrokeWidth(thickness);

        if (isInEditMode()) {
            loadDemoValues();
        }
    }

    public void loadDemoValues() {
        Plot plot1 = new Plot(Color.RED);
        addPlot("", plot1);
        mNumPoints = 1000; // (int)(getWidth() / mDefinition);
        for (int i = 0; i < mNumPoints; i++) {
            plot1.plotValues.add(((float) Math.random() * 220));
        }
    }

    public void addPlot(String plotName, Plot plot) {
        plots.put(plotName, plot);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
        mHeight = h;

        mNumPoints = (int) (mWidth / mDefinition);

        // zeroed the plot
        Iterator it = plots.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry m = (Map.Entry) it.next();
            // System.out.println(pairs.getKey() + " = " + pairs.getValue());
            // it.remove(); // avoids mContext ConcurrentModificationException
            Plot p = (Plot) m.getValue();
            p.zero();
        }

        if (mWidth < mHeight) {
            mMaxValue = w;
        } else {
            mMaxValue = w - 50;
        }

        mLastX = mMaxValue;

        mCanvas = new Canvas();

        mPaint.setColor(0x88000000);
        mPaint.setStyle(Paint.Style.FILL);

        mCanvas.drawRect(new RectF(0, 0, mWidth, mHeight), mPaint);

        // draw grid
        mPaint.setColor(0x55FFFFFF);
        for (int i = 0; i < mWidth; i = i + (int) (mWidth / 9)) {
            mCanvas.drawLine(i, 0, i, mHeight, mPaint);
        }
        for (int j = 0; j < mHeight; j = j + (int) (mHeight / 5)) {
            mCanvas.drawLine(0, j, mWidth, j, mPaint);

        }

        mReady = true;

        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        synchronized (this) {

            Iterator it = plots.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry m = (Map.Entry) it.next();
                Plot p = (Plot) m.getValue();

                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setColor(p.color);
                mPaint.setStyle(Paint.Style.STROKE);

                int i = 0;

                float y = 0;
                float y0 = 0;
                Path path = new Path();
                for (int x = 0; x < mWidth; x += mDefinition) {


                    y = CanvasUtils.map(p.plotValues.get(i), mMinBoundary, mMaxBoundary, mHeight, 0f);

                    if (x == 0) {
                        path.moveTo(x, y);
                    }

                    path.lineTo(x, y);

                    if (i < mNumPoints - 1) {
                        i++;
                    }

                }
                canvas.drawPath(path, mPaint);


                // show value
                // mPaint.setColor(Color.BLACK);

                // float lastVal =
                // p.plotValues.get(plots.get(0).plotValues.size() - 1);
                // canvas.drawText("" + lastVal, mWidth - 20, y, mPaint);

            }

            // draw border
            // mPaint.setColor(0xFF000000);
            // mPaint.setStyle(Styler.STROKE);
            // mCanvas.drawRoundRect(new RectF(0, 0, mWidth, mHeight), 0, 0,
            // mPaint);

        }
    }


    @ProtoMethod(description = "Init a plot name", example = "")
    @ProtoMethodParam(params = {""})
    public PPlotView_ init(String plotName) {

        if (plots.containsKey(plotName) == false) {
            plots.put(plotName, new Plot(Color.BLUE));
        }
        invalidate();

        return this;
    }


    @ProtoMethod(description = "Updates the value of the plotName plot", example = "")
    @ProtoMethodParam(params = {"plotName", "value"})
    public PPlotView_ update(String plotName, float v1) {

        if (mReady) {
            init(plotName);

            // shift points
            Plot p = plots.get(plotName);
            p.plotValues.remove(0);
            p.plotValues.add(v1);

            // check if is min or max
            if (v1 >= mMaxValue) {
                mMaxValue = v1;
            }
            if (v1 < mMinValue) {
                mMinValue = v1;
            }

            invalidate();
        }

        return this;
    }


    @ProtoMethod(description = "Updates the value of the default plot", example = "")
    @ProtoMethodParam(params = {"value"})
    public PPlotView_ update(float v) {
        update("default", v);

        return this;
    }

    @ProtoMethod(description = "Changes the plot limits", example = "")
    @ProtoMethodParam(params = {"min", "max"})
    public PPlotView_ range(float min, float max) {
        mMinBoundary = min;
        mMaxBoundary = max;

        return this;
    }

    @ProtoMethod(description = "Changes the plot definition", example = "")
    @ProtoMethodParam(params = {"definition"})
    public PPlotView_ definition(int definition) {
        mDefinition = definition;
        return this;
    }

    public void destroy() {
        //if (bitmap != null) {
        //	bitmap.recycle();
        //}
    }

    public class Plot {

        // values to plot
        public Vector<Float> plotValues;
        int color;

        public Plot(int color) {
            plotValues = new Vector<Float>();
            this.color = color;

            zero();
        }

        public void zero() {
            plotValues.clear();
            for (int i = 0; i < mNumPoints; i++) {
                plotValues.add(0f);
            }
        }

    }


    @ProtoMethod(description = "Sets the line thickness", example = "")
    @ProtoMethodParam(params = {"thickness"})
    public PPlotView_ thickness(float r) {
        thickness = r; // when dot
        mPaint.setStrokeWidth(thickness); // when line
        return this;
    }


    @ProtoMethod(description = "Changes the plot color", example = "")
    @ProtoMethodParam(params = {"plotName", "colorHex"})
    public PPlotView_ color(String plotName, String rgb) {
        new Color();
        int c = Color.parseColor(rgb);

        Plot p = plots.get(plotName);
        if (p != null) {
            p.color = c;
        }

        return this;
    }


    @ProtoMethod(description = "Get the plot array size", example = "")
    @ProtoMethodParam(params = {""})
    public int size() {
        return mNumPoints;
    }


    @ProtoMethod(description = "Get the current values of the plot as an array", example = "")
    @ProtoMethodParam(params = {"plotName"})
    public Float[] getArray(String plotName) {
        Plot p = plots.get(plotName);
        //MLog.d(TAG, "plot " + p);

        return p.plotValues.toArray(new Float[p.plotValues.size()]);
    }


    @ProtoMethod(description = "Set an array of values in the plot", example = "")
    @ProtoMethodParam(params = {"plotName", "value"})
    public PPlotView_ setArray(String plotName, float[] values) {
        Plot p = plots.get(plotName);

        MLog.d(TAG, p.plotValues.size() + " " + values.length);

        for (int i = 0; i < p.plotValues.size(); i++) {
            p.plotValues.set(i, values[i]);
            // p.plotValues.clear();
            // p.plotValues.set
        }
        invalidate();

        return this;
    }


    @ProtoMethod(description = "Sets the background color", example = "")
    @ProtoMethodParam(params = {"colorHex"})
    public PPlotView_ setBackground(String c) {
        super.setBackgroundColor(Color.parseColor(c));

        return this;
    }


}
