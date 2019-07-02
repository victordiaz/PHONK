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
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import java.lang.reflect.InvocationTargetException;

import io.phonk.runner.apidoc.annotation.ProtoMethod;
import io.phonk.runner.apidoc.annotation.ProtoMethodParam;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.base.utils.AndroidUtils;
import io.phonk.runner.base.utils.MLog;

public class PAbsoluteLayout extends FixedLayout {

    private static final String TAG = PAbsoluteLayout.class.getSimpleName();

    private AppRunner mAppRunner;

    private static final int PIXELS = 0;
    private static final int DP = 1;
    private static final int NORMALIZED = 2;
    private int mode = NORMALIZED;

    public int mWidth = -1;
    public int mHeight = -1;
    private Context mContext;

    public PAbsoluteLayout(AppRunner appRunner) {
        super(appRunner.getAppContext());
        mAppRunner = appRunner;
        mContext = appRunner.getAppContext();

        int w = (int) appRunner.pDevice.info().get("screenWidth");
        int h = (int) appRunner.pDevice.info().get("screenHeight");

        int statusBar = getStatusBarHeight();

        if (appRunner.pApp.settings.get("orientation").equals("landscape")) {
            if (w > h) { mWidth = w; mHeight = h; }
            else { mWidth = h; mHeight = w; }

            if (appRunner.pApp.settings.get("screen_mode").equals("fullscreen")) {
                int navigationBar = getNavigationBarSize(getContext()).x;
                mWidth += navigationBar;
            }
        } else {
            mWidth = w;
            mHeight = h - AndroidUtils.dpToPixels(getContext(), 24);


            if (appRunner.pApp.settings.get("screen_mode").equals("fullscreen")) {
                int navigationBar = getNavigationBarSize(getContext()).y;
                mHeight += statusBar + navigationBar;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        MLog.d(TAG, l + " " + t + " " + r + " " + b);
        // mWidth = t;
        // mHeight = b;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // MLog.d(TAG, w + " " + h);

        // mWidth = w;
        // mHeight = h;
    }

    @ProtoMethod(description = "Sets the background color", example = "")
    @ProtoMethodParam(params = {"colorHex"})
    public void backgroundColor(String c) {
        this.setBackgroundColor(Color.parseColor(c));
    }


    private int sizeToPixels(Object val, int toValue) {
        int returnVal = -1;

        if (val instanceof String) {
            // MLog.d(TAG, "oo is string");
            String str = (String) val;
            String[] splitted = str.split("(?<=\\d)(?=[a-zA-Z%])|(?<=[a-zA-Z%])(?=\\d)");

            double value = Double.parseDouble(splitted[0]);
            String type = splitted[1];

            returnVal = transform(type, value, toValue);
        } else if (val instanceof Double) {
            // MLog.d(TAG, "oo is double " + val);

            returnVal = transform("", (Double) val, toValue);
        }

        return returnVal;

    }

    private int transform(String type, Double value, int toValue) {
        // MLog.d(TAG, "oo transform");

        int retValue = -1;

        switch (type) {
            case "px":
                retValue = value.intValue();
                break;
            case "dp":
                // MLog.d(TAG, "retValue dp " + value + " " + retValue);
                retValue = AndroidUtils.dpToPixels(mContext, value.intValue());
                MLog.d(TAG, "retValue dp " + value + " " + retValue);
                break;
            case "":
                retValue = (int) (value * toValue);
                // MLog.d(TAG, "retValue ''" + value + " " + retValue);
                break;
            /*
            case "%":
                retValue = (int) (value / 100.0f * toValue);
                break;
            */
            case "w":
                retValue = (int) (value * mWidth);
                break;
            case "h":
                retValue = (int) (value * mHeight);
                break;
            default:
                break;
        }

        // MLog.d(TAG, "oo --> " + retValue);
        return retValue;
    }


    @ProtoMethod(description = "Adds a view", example = "")
    @ProtoMethodParam(params = {"view", "x", "y", "w", "h"})
    public void addView(View v, Object x, Object y, Object w, Object h) {
        MLog.d(TAG, "adding view (normalized) -> " + x + " " + y + " " + w + " "  + h);

        int mx = sizeToPixels(x, mWidth);
        int my = sizeToPixels(y, mHeight);
        int mw = sizeToPixels(w, mWidth);
        int mh = sizeToPixels(h, mHeight);

        if (mw < 0) mw = LayoutParams.WRAP_CONTENT;
        if (mh < 0) mh = LayoutParams.WRAP_CONTENT;

        MLog.d(TAG, "adding a view (denormalized) -> " +  v + " in " + mx + " " + my + " " + mw + " " + mh);
        addView(v, new LayoutParams(mw, mh, mx, my));

    }

    public void mode(String type) {
        switch (type) {
            case "px":
                this.mode = PIXELS;
                break;
            case "dp":
                this.mode = DP;
                break;
            case "normalized":
                this.mode = NORMALIZED;
                break;
            default:
                this.mode = NORMALIZED;
        }
    }

    public int width() {
        return mWidth;
    }

    public int height() {
        return mHeight;
    }


    /**
     * This is what we use to actually position and size the views
     */
    /*
    protected void positionView(View v, int x, int y, int w, int h) {
		if (w == -1) {
			w = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		}
		if (h == -1) {
			h = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		}
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(w, h);
		params.leftMargin = x;
		params.topMargin = y;
		v.setLayoutParams(params);
	}
	*/


    public static Point getNavigationBarSize(Context context) {
        Point appUsableSize = getAppUsableScreenSize(context);
        Point realScreenSize = getRealScreenSize(context);

        // navigation bar on the right
        if (appUsableSize.x < realScreenSize.x) {
            return new Point(realScreenSize.x - appUsableSize.x, appUsableSize.y);
        }

        // navigation bar at the bottom
        if (appUsableSize.y < realScreenSize.y) {
            return new Point(appUsableSize.x, realScreenSize.y - appUsableSize.y);
        }

        // navigation bar is not present
        return new Point();
    }

    public static Point getAppUsableScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public static Point getRealScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();

        if (Build.VERSION.SDK_INT >= 17) {
            display.getRealSize(size);
        } else if (Build.VERSION.SDK_INT >= 14) {
            try {
                size.x = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
                size.y = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
            } catch (IllegalAccessException e) {} catch (InvocationTargetException e) {} catch (NoSuchMethodException e) {}
        }

        return size;
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getContext().getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

}
