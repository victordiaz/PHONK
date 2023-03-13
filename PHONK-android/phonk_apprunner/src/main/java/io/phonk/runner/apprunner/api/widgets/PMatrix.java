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
public class PMatrix extends PCustomView {
    private static final String TAG = PMatrix.class.getSimpleName();
    public final MatrixStyler styler;
    private final int colorUnselected;
    int COLS = 20;
    int ROWS = 20;
    boolean wasSelected = false;
    private int[][] matrix;
    private int colorSelected;
    private float W;
    private float H;
    private ReturnInterface callback;
    private float selectedColumn = -1;
    final OnDrawCallback mydraw = new OnDrawCallback() {
        @Override
        public void event(PCanvas c) {
            c.clear();
            c.cornerMode(true);

            W = (float) c.width / (float) COLS;
            H = (float) c.height / (float) ROWS;

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

    public PMatrix(AppRunner appRunner, Map initProps) {
        super(appRunner, initProps);
        MLog.d(TAG, "create matrix");

        draw = mydraw;

        styler = new MatrixStyler(appRunner, this, props);
        props.onChange((name, value) -> {
            WidgetHelper.applyViewParam(name, value, props, this, appRunner);
            styler.apply(name, value);
        });

        props.eventOnChange = false;
        props.put("matrixCellColor", "#00FFFFFF");
        props.put("matrixCellSelectedColor", appRunner.pUi.theme.get("primary"));

        props.put("borderColor", appRunner.pUi.theme.get("secondaryShade"));
        props.put("borderRadius", 0);

        props.put("matrixCellBorderSize", appRunner.pUtil.dpToPixels(1));
        props.put("matrixCellBorderColor", appRunner.pUi.theme.get("secondaryShade"));
        props.put("matrixCellBorderRadius", 0);
        props.put("background", "#00FFFFFF");
        props.put("backgroundPressed", "#00FFFFFF");
        WidgetHelper.fromTo(initProps, props);
        props.eventOnChange = true;
        props.change();

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
            // MLog.d(TAG, "matrix " + i + " " + j + " " + newMatrix[i][j]);
            System.arraycopy(matrix[i], 0, newMatrix[i], 0, this.matrix[i].length);
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

    public PMatrix highlightColumn(int column) {
        if (column < 0) column = -1;
        if (column > COLS) column = -1;

        selectedColumn = column;

        invalidate();
        return this;
    }

    public PMatrix xy(int x, int y, boolean selected) {
        if (isInBoundaries(x, y)) return this;

        if (selected) matrix[x][y] = colorSelected;
        else matrix[x][y] = colorUnselected;
        invalidate();

        return this;
    }

    private boolean isInBoundaries(int x, int y) {
        return x < 0 || x >= COLS || y < 0 || y >= ROWS;
    }

    public PMatrix xy(int x, int y, String color) {
        if (isInBoundaries(x, y)) return this;
        matrix[x][y] = Color.parseColor(color);
        invalidate();

        return this;
    }

    public boolean xy(int x, int y) {
        if (isInBoundaries(x, y)) return false;
        return matrix[x][y] == colorSelected;
    }

    public PMatrix onChange(final ReturnInterface callbackfn) {
        this.callback = callbackfn;

        return this;
    }

    static class MatrixStyler extends Styler {
        int matrixCellColor;
        int matrixCellSelectedColor;
        float matrixCellBorderSize;
        int matrixCellBorderColor;

        MatrixStyler(AppRunner appRunner, View view, PropertiesProxy props) {
            super(appRunner, view, props);
        }

        @Override
        public void apply(String name, Object value) {
            super.apply(name, value);

            if (name == null) {
                apply("matrixCellColor");
                apply("matrixCellSelectedColor");
                apply("matrixCellBorderSize");
                apply("matrixCellBorderColor");

            } else {
                if (value == null) return;
                switch (name) {
                    case "matrixCellColor":
                        matrixCellColor = Color.parseColor(value.toString());
                        break;

                    case "matrixCellSelectedColor":
                        matrixCellSelectedColor = Color.parseColor(value.toString());
                        break;

                    case "matrixCellBorderSize":
                        matrixCellBorderSize = toFloat(value);
                        break;

                    case "matrixCellBorderColor":
                        matrixCellBorderColor = Color.parseColor(value.toString());
                        break;
                }
            }
        }
    }




}
