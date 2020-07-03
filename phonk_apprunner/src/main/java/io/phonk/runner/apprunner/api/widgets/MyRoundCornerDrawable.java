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
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Build;

import io.phonk.runner.base.utils.AndroidUtils;

public class MyRoundCornerDrawable extends Drawable {

    private Paint mPaintBg;
    private final Paint mPaintBorder;
    private float mRadius = 22;

    public MyRoundCornerDrawable(/* Bitmap bitmap */) {
        /*
        BitmapShader shader;
        shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP,
                Shader.TileMode.CLAMP);
        */

        // background
        mPaintBg = new Paint();
        mPaintBg.setStyle(Paint.Style.FILL);
        mPaintBg.setStrokeWidth(0);
        // mPaintBg.setShadowLayer(10, 22, 22, Color.BLACK);
        mPaintBg.setAntiAlias(true);

        // border
        mPaintBorder = new Paint();
        mPaintBorder.setStyle(Paint.Style.STROKE);
        // mPaintBorder.setAntiAlias(true);

        // mPaintBg.setShader(shader);
    }

    @Override
    public void draw(Canvas canvas) {
        int height = getBounds().height();
        int width = getBounds().width();

        // allow drawing out of bounds vertically
        Rect clipBounds = canvas.getClipBounds();
        clipBounds.inset(-12, -12);
        // canvas.clipRect(clipBounds, Region.Op.DIFFERENCE);

        RectF rect = new RectF(0.0f, 0.0f, width, height);

        // background
        canvas.drawRoundRect(rect, mRadius, mRadius, mPaintBg);

        // border
        canvas.drawRoundRect(rect, mRadius, mRadius, mPaintBorder);
    }

    @Override
    public void setAlpha(int alpha) {
        mPaintBg.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaintBg.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public void setBackground(int c) {
        this.mPaintBg.setColor(c);

        int c1 = Color.argb(100, 0, 252, 0);
        int c2 = Color.argb(100, 0, 252, 0);
        // mPaintBg.setAntiAlias(true);

        // Android < Lollipop doesnt like so transparency in the background
        // a shader makes the trick
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mPaintBg.setShader(new LinearGradient(0, 0, 0, 150, c, c, Shader.TileMode.CLAMP));
        }

        invalidateSelf();
    }

    public void setBorderColor(int c) {
        this.mPaintBorder.setColor(c);
        invalidateSelf();
    }

    public void setBorderWidth(int w) {
        this.mPaintBorder.setStrokeWidth(w);
        invalidateSelf();
    }

    public void setBorderRadius(int r) {
        mRadius = r;
        invalidateSelf();
    }

}

