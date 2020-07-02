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
import android.os.Handler;
import android.os.SystemClock;

import java.util.ArrayList;
import java.util.Map;

import io.phonk.runner.apidoc.annotation.PhonkClass;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.base.utils.AndroidUtils;
import io.phonk.runner.base.utils.MLog;

@PhonkClass
public class PPlot extends PCustomView implements PViewMethodsInterface {
    private static final String TAG = PPlot.class.getSimpleName();

    private final Handler handler;
    private final Runnable r;
    private final int mStrokeWeight;

    StyleProperties props = new StyleProperties();
    private Styler styler;

    private ArrayList<PlotPoint> arrayData = new ArrayList<>();
    public ArrayList<PlotPoint> arrayViz = new ArrayList<>();

    private float yMax = Float.MIN_VALUE;
    private float yMin = Float.MAX_VALUE;
    private float xMax = Float.MIN_VALUE;
    private float xMin = Float.MAX_VALUE;

    String name = "";
    private boolean isRange = false;
    private int mWidth;
    private int mHeight;

    public PPlot(AppRunner appRunner) {
        super(appRunner);

        draw = mydraw;
        styler = new Styler(appRunner, this, props);
        styler.apply();

        mAppRunner.whatIsRunning.add(this);

        mStrokeWeight = AndroidUtils.dpToPixels(mAppRunner.getAppContext(), 1);

        MLog.d(TAG, "starting runnable");

        handler = new Handler();
        r = new Runnable() {
            @Override
            public void run() {
                // exit if no data
                if (arrayData.size() > 0) {
                    MLog.d(TAG, "runnable " + arrayData.size());
                    arrayViz.clear();

                    // if auto scale
                    float xfrom = arrayData.get(0).x;
                    float xto = xMax;
                    float yfrom = yMax;
                    float yto = yMin;

                    for (int i = 0; i < arrayData.size(); i++) {
                        PlotPoint p = arrayData.get(i);

                        float x = mAppRunner.pUtil.map(p.x, xfrom, xto, 0 + 10, mWidth - 10);
                        float y = mAppRunner.pUtil.map(p.y, yfrom, yto, 0 + 20, mHeight - 20);
                        arrayViz.add(new PlotPoint(x, y));

                        // MLog.d(TAG, width + " " + height);
                        // MLog.d(TAG, "arrayData: " + i + " " + p.x + " " + p.y + " -> " + x + ", " + y);
                    }
                }

                handler.postDelayed(r, 10);
                postInvalidate();
            }
        };

        handler.post(r);
    }

    // plot color
    // plot thickness
    // plot background
    // rangeX [x1, x2]
    // rangeY [y1, y2]

    OnDrawCallback mydraw = new OnDrawCallback() {
        @Override
        public void event(PCanvas c) {
            mWidth = c.width;
            mHeight = c.height;
            // MLog.d(TAG, "paint ");

            c.clear();
            c.cornerMode(false);

            /*
            Shader shader = new LinearGradient(0, 0, 0, c.height, new int[] {Color.parseColor("#00000000"), Color.parseColor("#000000") }, null, Shader.TileMode.MIRROR);
            Matrix matrix = new Matrix();
            matrix.setRotate(90);
            shader.setLocalMatrix(matrix);
            c.mPaintFill.setShader(shader);
            */

            c.stroke("#55000000"); // styler.plotColor);
            c.strokeWidth(mStrokeWeight);           // center line
            c.line(0, c.height / 2, c.width, c.height / 2);

            if (!arrayViz.isEmpty()) {
                PlotPoint p;

                c.strokeWidth(AndroidUtils.dpToPixels(mAppRunner.getAppContext(), 2)); // styler.plotWidth);
                c.noFill();
                c.strokeWidth(styler.plotWidth);
                c.stroke(styler.plotColor);

                c.beginPath();
                p = arrayViz.get(0);
                c.pointPath(p.x, p.y);

                for (int i = 0; i < arrayViz.size(); i++) {
                    p = arrayViz.get(i);
                    c.pointPath(p.x, p.y);
                }

                p = arrayViz.get(arrayViz.size() - 1);
                c.pointPath(p.x, p.y);
                c.closePath();

                if (false) {
                    c.fill(Color.BLUE);
                    c.noStroke();
                    for (int i = 0; i < arrayViz.size(); i++) {
                        p = arrayViz.get(i);
                        c.text("" + p.y, p.x, p.y);
                    }
                }

            }

            // text
            c.textSize(AndroidUtils.spToPixels(mAppRunner.getAppContext(), 12));
            c.fill(styler.textColor);
            c.noStroke();
            c.textAlign("right");

            c.text(name, mWidth - 20, 50);

            c.noFill();
            c.stroke(styler.borderColor);
            c.strokeWidth(5);
            c.cornerMode(true);
            c.rect(0, 0, c.width, c.height);
        }
    };

    public PPlot update(float y) {
        this.update(now(), y);

        return this;
    }

    public PPlot range(float min, float max) {
        yMin = min;
        yMax = max;
        isRange = true;

        return this;
    }

    public PPlot update(float x, float y) {
        if (!isRange) {
            if (y < yMin) yMin = y;
            else if (y > yMax) yMax = y;
        }
        if (x < xMin) xMin = x;
        else if (x > xMax) xMax = x;


        // MLog.d(TAG, "adding " + x + " " + y);

        if (arrayData.size() > 100) {
            arrayData.remove(0);
        }

        arrayData.add(new PlotPoint(x, y));

        return this;
    }

    /*
    public void map(float[] xAxis, ReturnInterfaceWithReturn callback) {
        ReturnObject r = new ReturnObject();

        float[][] xyAxis = new float[xAxis.length][2];
        for (int i = 0; i < xAxis.length; i++) {
            xyAxis[i][0] = xAxis[i];
        }

        r.put("xyAxis", xyAxis);
        array2d = (float[][]) callback.event(r);
        invalidate();
    }
    */

    public void array2d(float[][] val) {
        arrayViz.clear();

        for (int i = 0; i < val.length; i++) {
            arrayData.add(new PlotPoint(val[i][0], val[i][1]));
        }

        invalidate();
    }

    public PPlot name(String name) {
        this.name = name;
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
    public Map getProps() {
        return props;
    }

    public void __stop() {
        handler.removeCallbacks(r);
    }

    private long now() {
        return SystemClock.uptimeMillis();
    }

    class PlotPoint {
        float x;
        float y;

        public PlotPoint(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }
}
