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
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;

public class SeekBarProgressDrawable extends ClipDrawable {

    private Paint mPaint = new Paint();
    private float dy;
    private Rect mRect;


    public SeekBarProgressDrawable(Drawable drawable, int gravity, int orientation, Context ctx) {
        super(drawable, gravity, orientation);
        mPaint.setColor(Color.WHITE);
        dy = 10; // ctx.getResources().getDimension(R.dimen.two_dp);
    }

    @Override
    public void draw(Canvas canvas) {
        if (mRect == null) {
            mRect = new Rect(getBounds().left, (int) (getBounds().centerY() - dy / 2), getBounds().right, (int) (getBounds().centerY() + dy / 2));
            setBounds(mRect);
        }

        super.draw(canvas);
    }


    @Override
    public void setAlpha(int i) {
        mPaint.setAlpha(i);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}