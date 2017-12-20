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

package io.phonk.gui._components;


import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

public class WTFRelativeLayout extends RelativeLayout {
    private static final String TAG = WTFRelativeLayout.class.getSimpleName();
    private ValueAnimator va;

    RectF rectf;

    public WTFRelativeLayout(Context context) {
        super(context);
    }

    public WTFRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WTFRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
//
//        MLog.d(TAG, changed + " " + l + " " + t + " " + r + " " + b);
//
//        rectf = new RectF(l, t, r, b);
//
//        va = ValueAnimator.ofFloat(rectf.height(), 0);
//        va.setDuration(2000);
//        va.setInterpolator(new AccelerateDecelerateInterpolator());
//
//        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            public void onAnimationUpdate(ValueAnimator animation) {
//                Float value = (Float) animation.getAnimatedValue();
////                rectf.bottom = 200; //value;
//                MLog.d(TAG, "" + value);
//                invalidate();
//            }
//        });
//        va.start();
//
//        View button = (Button) findViewById(R.id.selectFolderButton);
//        float bx = button.getX();
//        float by = button.getY();
//        int bw = button.getWidth();
//        int bh = button.getHeight();
//
//        MLog.d(TAG, "button " + bx + " " + by + " " + bw + " " + bh);
    }

    @Override
    protected void onDraw(Canvas canvas) {

//        Path clipPath = new Path();
//        clipPath.addRoundRect(rectf, 0, 0, Path.Direction.CW);
//        canvas.clipPath(clipPath);

//        BitmapShader bitmapShader = new BitmapShader(<original drawable>, TileMode.CLAMP, TileMode.CLAMP);
//
//        Paint paint = new Paint();
//        paint.setAntiAlias(true);
//        paint.setColor(0xFFFFFFFF);
//        //paint.setShader(bitmapShader);
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
//
//        canvas.drawRoundRect(new RectF(0, 0, 500, 500), 0, 0, paint);
//

        super.onDraw(canvas);
    }
}
