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

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import java.lang.reflect.InvocationTargetException;

import io.phonk.runner.apidoc.annotation.PhonkMethod;
import io.phonk.runner.apidoc.annotation.PhonkMethodParam;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.base.utils.AndroidUtils;
import io.phonk.runner.base.utils.MLog;

public class PAbsoluteLayout extends FixedLayout {

    private static final String TAG = PAbsoluteLayout.class.getSimpleName();
    private final AppRunner mAppRunner;
    private int mWidth = -1;
    private int mHeight = -1;

    public PAbsoluteLayout(AppRunner appRunner) {
        super(appRunner.getAppContext());
        mAppRunner = appRunner;

        int w = (int) appRunner.pDevice.info().get("screenWidth");
        int h = (int) appRunner.pDevice.info().get("screenHeight");

        int statusBar = getStatusBarHeight();

        // MLog.d(TAG, appRunner.pApp.settings.get("orientation") + " " + w + " " + h);

        if (appRunner.pApp.settings.get("orientation").equals("landscape")) {
            if (w > h) {
                mWidth = w;
                mHeight = h;
            } else {
                mWidth = h;
                mHeight = w;
            }

            if (appRunner.pApp.settings.get("screen_mode").equals("fullscreen")) {
                // int navigationBar = 0;getNavigationBarSize(getContext()).x;
                // mWidth += navigationBar;
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

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getContext().getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * This is what we use to actually position and size the views
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
            } catch (IllegalAccessException ignored) {
            } catch (InvocationTargetException ignored) {
            } catch (NoSuchMethodException ignored) {
            }
        }

        return size;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        MLog.d(TAG, l + " " + t + " " + r + " " + b);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

    }

    @PhonkMethod(description = "Sets the background color", example = "")
    @PhonkMethodParam(params = {"colorHex"})
    public void backgroundColor(String c) {
        this.setBackgroundColor(Color.parseColor(c));
    }

    @PhonkMethod(description = "Adds a view", example = "")
    @PhonkMethodParam(params = {"view", "x", "y", "w", "h"})
    public void addView(View v, Object x, Object y, Object w, Object h) {
        if (v instanceof PViewMethodsInterface) {
            PropertiesProxy map = (PropertiesProxy) ((PViewMethodsInterface) v).getProps();
            map.eventOnChange = false;
            map.put("x", x);
            map.put("y", y);
            map.put("w", w);
            map.put("h", h);
            map.eventOnChange = true;
        }

        int mx = mAppRunner.pUtil.sizeToPixels(x, mWidth);
        int my = mAppRunner.pUtil.sizeToPixels(y, mHeight);
        int mw = mAppRunner.pUtil.sizeToPixels(w, mWidth);
        int mh = mAppRunner.pUtil.sizeToPixels(h, mHeight);

        addView(v, new LayoutParams(mw, mh, mx, my));
    }

    public int width() {
        return mWidth;
    }

    public int height() {
        return mHeight;
    }

}
