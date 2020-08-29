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
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Map;

import io.phonk.runner.apidoc.annotation.PhonkClass;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.api.common.ReturnInterface;
import io.phonk.runner.apprunner.api.common.ReturnObject;
import io.phonk.runner.base.utils.MLog;

@PhonkClass
public class PMatrix extends PCustomView implements PViewMethodsInterface {
    private static final String TAG = PMatrix.class.getSimpleName();

    public StylePropertiesProxy props = new StylePropertiesProxy();
    public MatrixStyler styler;

    private ArrayList touches;
    private float x;
    private float y;
    private int[][] matrix;
    private int colorSelected;
    private int colorUnselected;

    int COLS = 20;
    int ROWS = 20;

    private float W;
    private float H;
    private int mWidth;
    private int mHeight;
    private static final int STATUS_DISABLED = 0;
    private static final int STATUS_ENABLED = 1;

    private ReturnInterface callback;
    private float selectedColumn = -1;

    public PMatrix(AppRunner appRunner, Map initProps) {
        super(appRunner, initProps);
        MLog.d(TAG, "create matrix");

        draw = mydraw;

        styler = new MatrixStyler(appRunner, this, props);
        props.eventOnChange = false;
        props.put("matrixCellColor", props, "#00FFFFFF");
        props.put("matrixCellSelectedColor", props, appRunner.pUi.theme.get("primary"));

        props.put("borderColor", props, appRunner.pUi.theme.get("secondaryShade"));
        props.put("borderRadius", props, 0);

        props.put("matrixCellBorderSize", props, appRunner.pUtil.dpToPixels(1));
        props.put("matrixCellBorderColor", props, appRunner.pUi.theme.get("secondaryShade"));
        props.put("matrixCellBorderRadius", props, 0);
        props.put("background", props, "#00FFFFFF");
        props.put("backgroundPressed", props, "#00FFFFFF");
        styler.fromTo(initProps, props);
        props.eventOnChange = true;
        styler.apply();

        colorUnselected = styler.matrixCellColor;
        colorSelected = styler.matrixCellSelectedColor;
    }

    public PMatrix size(int cols, int rows) {
        COLS = cols;
        ROWS = rows;

        matrix = new int[COLS][ROWS];
        clear();

        return this;
    }

    public void clear() {
        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLS; x++) {
                matrix[x][y] = colorUnselected;
            }
        }
        invalidate();
    }

    public int[][] get() {
        int[][] newMatrix = new int[COLS][ROWS];

        for (int i = 0; i < this.matrix.length; i++) {
            for (int j = 0; j < this.matrix[i].length; j++) {
                // MLog.d(TAG, "matrix " + i + " " + j + " " + newMatrix[i][j]);
                newMatrix[i][j] = matrix[i][j];
            }
        }

        return newMatrix;
    }

    public PMatrix set(int[][] newMatrix) {
        MLog.d(TAG, "matrix size" + newMatrix.length + " " + newMatrix[0].length);

        for (int i = 0; i < newMatrix.length; i++) {
            for (int j = 0; j < newMatrix[i].length; j++) {
                MLog.d(TAG, "matrix " + i + " " + j + " " + newMatrix[i][j]);
                this.matrix[i][j] = newMatrix[i][j];
            }
        }

        invalidate();

        return this;
    }

    boolean wasSelected = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        int tx = (int) (x / W);
        int ty = (int) (y / H);

        // if outside boundaries
        if (!(tx < COLS && ty < ROWS && tx >= 0 && ty >= 0)) return true;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                wasSelected = matrix[tx][ty] != colorUnselected;

                // if already selected we unselect the cell
                if (wasSelected) matrix[tx][ty] = colorUnselected;
                else matrix[tx][ty] = colorSelected;

                MLog.d(TAG, "wasSelected " + wasSelected + " " + matrix[tx][ty] + " " + colorSelected);

                return true;
            case MotionEvent.ACTION_MOVE:
                if (!wasSelected /* matrix[tx][ty] != colorSelected */) matrix[tx][ty] = colorSelected;
                else matrix[tx][ty] = colorUnselected;
                // break;

            case MotionEvent.ACTION_UP:
                break;
            default:
                return false;
        }

        if (callback != null) {
            ReturnObject ret = new ReturnObject();
            ret.put("x", tx);
            ret.put("y", ty);
            ret.put("selected", !wasSelected);
            ret.put("color", matrix[tx][ty]);
            callback.event(ret);
        }

        invalidate();

        return true;
    }

    public void reset() {
        for (int i = 0; i < COLS; i++) {
            for (int j = 0; j < ROWS; j++) {
                // matrix[i][j] = false;
            }
        }
    }

    public void colorCell(String c) {
        colorSelected = Color.parseColor(c);
    }

    /*
    public void selectColumn(int m) {
        for (int i = 0; i < M; i++) matrix[m][i] = true;
    }

    public void selectRow(int n) {
        for (int i = 0; i < N; i++) matrix[i][n] = true;
    }
     */

    public PMatrix highlightColumn(int column) {
        if (column < 0) column = -1;
        if (column > COLS) column = -1;

        selectedColumn = column;

        invalidate();
        return this;
    }

    public PMatrix xy(int x, int y, boolean selected) {
        if (!isInBoundaries(x, y)) return this;

        if (selected) matrix[x][y] = colorSelected;
        else matrix[x][y] = colorUnselected;
        invalidate();

        return this;
    }

    public PMatrix xy(int x, int y, String color) {
        if (!isInBoundaries(x, y)) return this;
        matrix[x][y] = Color.parseColor(color);
        invalidate();

        return this;
    }
    public boolean xy(int x, int y) {
        if (!isInBoundaries(x, y)) return false;
        return matrix[x][y] == colorSelected;
    }

    private boolean isInBoundaries(int x, int y) {
        if (x >= 0 && x < COLS && y >= 0 && y < ROWS) return true;
        else return false;
    }

    OnDrawCallback mydraw = new OnDrawCallback() {
        @Override
        public void event(PCanvas c) {
            mWidth = c.width;
            mHeight = c.height;

            c.clear();
            c.cornerMode(true);

            W = (float) c.width / (float) COLS;
            H = (float) c.height / (float) ROWS;

            // MLog.d(TAG, " " + W + " " + c.width + " " + M);
            // MLog.d(TAG, " " + H + " " + c.height + " " + N);

            c.stroke(styler.matrixCellBorderColor);
            c.strokeWidth(styler.matrixCellBorderSize);

            // grid
            for (int i = 0; i < COLS; i++) {
                for (int j = 0; j < ROWS; j++) {
                    c.fill(matrix[i][j]);
                    // c.rect(i * W, j * H, W, H, 2, 2);
                    c.point(i * W, j * H);
                }
            }

            // cells
            c.noStroke();
            for (int i = 0; i < COLS; i++) {
                for (int j = 0; j < ROWS; j++) {
                    c.fill(matrix[i][j]);
                    c.rect(i * W, j * H, W, H, 2, 2);
                    // c.point(i * W, j * H);
                }
            }

            // matrix border
            c.stroke(styler.borderColor);
            c.noFill();
            c.rect(0, 0, c.width, c.height, (float) styler.borderRadius, (float) styler.borderRadius);

            c.stroke("#00000022");
            c.fill("#2200FF00");

            if (selectedColumn != -1) {
                c.rect(W * selectedColumn, 0, W, c.height, 0, 0);
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
    public void setProps(Map style) {
        styler.setProps(style);
    }

    @Override
    public Map getProps() {
        return props;
    }


    class MatrixStyler extends Styler {
        int matrixCellColor;
        int matrixCellSelectedColor;
        float matrixCellBorderSize;
        int matrixCellBorderColor;
        float matrixCellBorderRadius;

        MatrixStyler(AppRunner appRunner, View view, StylePropertiesProxy props) {
            super(appRunner, view, props);
        }

        @Override
        public void apply() {
            super.apply();

            matrixCellColor = Color.parseColor(mProps.get("matrixCellColor").toString());
            matrixCellSelectedColor = Color.parseColor(mProps.get("matrixCellSelectedColor").toString());
            matrixCellBorderSize = toFloat(mProps.get("matrixCellBorderSize"));
            matrixCellBorderColor = Color.parseColor(mProps.get("matrixCellBorderColor").toString());
            matrixCellBorderRadius = toFloat(mProps.get("matrixCellBorderRadius"));
        }
    }
}
