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

import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Map;

import io.phonk.runner.api.common.ReturnInterface;
import io.phonk.runner.api.common.ReturnObject;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.StyleProperties;
import io.phonk.runner.base.utils.MLog;

/**
 * Created by biquillo on 11/09/16.
 */
public class PMatrix extends PCustomView implements PViewMethodsInterface {

    private static final String TAG = PMatrix.class.getSimpleName();

    public StyleProperties props = new StyleProperties();
    public Styler styler;

    private ArrayList touches;
    private float x;
    private float y;
    private boolean[][] matrix;

    int M = 20;
    int N = 20;

    private float W;
    private float H;
    private int mWidth;
    private int mHeight;
    private static final int STATUS_DISABLED = 0;
    private static final int STATUS_ENABLED = 1;

    boolean type = false;
    private ReturnInterface callback;

    public PMatrix(AppRunner appRunner, int m, int n) {
        super(appRunner);
        MLog.d(TAG, "create matrix");

        draw = mydraw;
        styler = new Styler(appRunner, this, props);
        styler.apply();

        M = m;
        N = n;

        matrix = new boolean[M][N];
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        int tx = (int) (x / W);
        int ty = (int) (y / H);

        // if outside boundaries
        if (!(tx < M && ty < N && tx >= 0 && ty >= 0)) return true;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                type = matrix[tx][ty];
                matrix[tx][ty] = !type;

                return true;
            case MotionEvent.ACTION_MOVE:
                if (matrix[tx][ty] == type) matrix[tx][ty] = !type;

                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                return false;
        }

        if (callback != null) {
            ReturnObject ret = new ReturnObject();
            ret.put("x", tx);
            ret.put("y", ty);
            ret.put("value", matrix[tx][ty]);
            callback.event(ret);
        }

        invalidate();

        return true;
    }

    public void reset() {
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                matrix[i][j] = false;
            }
        }
    }


    public void selectColumn (int m) {
        for (int i = 0; i < M; i++) matrix[m][i] = true;
    }

    public void selectRow (int n) {
        for (int i = 0; i < N; i++) matrix[i][n] = true;
    }

    OnDrawCallback mydraw = new OnDrawCallback() {
        @Override
        public void event(PCanvasM c) {
            mWidth = c.width;
            mHeight = c.height;

            c.clear();
            c.cornerMode(true);

            /* background
            c.fill(0, 225, 0);
            c.stroke(0, 0, 0);
            c.rect(0, 0, c.width, c.height);
            */

            W = (float) c.width / (float) M;
            H = (float) c.height / (float) N;

            // MLog.d(TAG, " " + W + " " + c.width + " " + M);
            // MLog.d(TAG, " " + H + " " + c.height + " " + N);

            c.stroke(styler.matrixCellBorderColor);
            c.strokeWidth(styler.matrixCellBorderSize);
            for (int i = 0; i < M; i++) {
                for (int j = 0; j < N; j++) {
                    // console.log(selected[i][j])
                    // console.log(selected[i][j])

                    // c.fill(0, 0, 0, 125);
                    // c.stroke(0, 0, 0, 0);
                    // c.strokeWidth(2);
                    // c.rect(i * W, j * H, W, H);

                    if (matrix[i][j] == false) c.fill(styler.matrixCellColor);
                    else c.fill(styler.matrixCellSelectedColor);

                    c.rect(i * W, j * H, W, H, 2, 2);
                }
            }
        }
    };


    public PMatrix onChange(final ReturnInterface callbackfn) {
        this.callback = callbackfn;

        return this;
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
