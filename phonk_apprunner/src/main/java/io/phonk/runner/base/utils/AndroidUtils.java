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

package io.phonk.runner.base.utils;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;

import io.phonk.runner.R;
import io.phonk.runner.apprunner.AppRunnerSettings;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class AndroidUtils {

    private static final String TAG = "AndroidUtils";

    public static void takeScreenshot(View v) {
        // create bitmap screen capture
        Bitmap bitmap;
        View v1 = v.getRootView();
        v1.setDrawingCacheEnabled(true);
        bitmap = Bitmap.createBitmap(v1.getDrawingCache());
        v1.setDrawingCacheEnabled(false);
    }

    public static Bitmap takeScreenshotView(View v) {
        // create bitmap screen capture
        v.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false);

        return bitmap;

    }

    public static Point getScreenSize(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

        int screenWidth = displayMetrics.widthPixels; //getActivity().getScrenSize().x;
        int screenHeight = displayMetrics.heightPixels; //getActivity().getScrenSize().y;

        Point size = new Point();
        size.x = screenWidth;
        size.y = screenHeight;

        return size;
    }

    public static int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public static int pixelsToDp(Context c, int px) {

        Resources resources = c.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);

        return (int) dp;
    }

    public static int dpToPixels(Context c, int dp) {

        Resources resources = c.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);

        return (int) px;
    }

    public static int pixelsToSp(Context c, int px) {

        Resources resources = c.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);

        return (int) dp;
    }

    public static int spToPixels(Context c, int sp) {

        Resources resources = c.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = sp * (metrics.densityDpi / 160f);

        return (int) px;
    }




    /**
     * Show an event in the LogCat view, for debugging
     */
    public static void dumpMotionEvent(MotionEvent event) {
        String[] names = {"DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE", "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?"};
        StringBuilder sb = new StringBuilder();
        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        sb.append("event ACTION_").append(names[actionCode]);
        if (actionCode == MotionEvent.ACTION_POINTER_DOWN || actionCode == MotionEvent.ACTION_POINTER_UP) {
            sb.append("(pid ").append(action >> MotionEvent.ACTION_POINTER_INDEX_SHIFT);
            sb.append(")");
        }
        sb.append("[");
        for (int i = 0; i < event.getPointerCount(); i++) {
            sb.append("#").append(i);
            sb.append("(pid ").append(event.getPointerId(i));
            sb.append(")=").append((int) event.getX(i));
            sb.append(",").append((int) event.getY(i));
            if (i + 1 < event.getPointerCount()) {
                sb.append(";");
            }
        }
        sb.append("]");
        MLog.d(TAG, sb.toString());
    }

    public static void setViewGenericShadow(View v, int w, int h) {
        if (isVersionLollipop()) {
            setViewGenericShadow(v, CLIP_RECT, 0, 0, w, h, 10);
        }
    }

    public static int CLIP_RECT = 0;
    public static int CLIP_ROUND = 1;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setViewGenericShadow(View v, int type, final int x, final int y, final int w, final int h, final int r) {
        // MLog.d("qq", "no android L " + Build.VERSION.SDK + " " + L);

        if (isVersionLollipop()) {

            ViewOutlineProvider viewOutlineProvider = null;

            //MLog.d("qq", "is android L");
            if (type == CLIP_RECT) {

                viewOutlineProvider = new ViewOutlineProvider() {
                    @Override
                    public void getOutline(View view, Outline outline) {
                        // Or read size directly from the view's width/height
                        outline.setRoundRect(new Rect(x, y, w, h), r);
                    }
                };

            } else if (type == CLIP_ROUND) {

                viewOutlineProvider = new ViewOutlineProvider() {
                    @Override
                    public void getOutline(View view, Outline outline) {
                        // Or read size directly from the view's width/height
                        outline.setOval(x, y, w, h);
                    }
                };


            } else {
                Path path = new Path();
                path.moveTo(10, 10);
                path.lineTo(100, 100);
                path.lineTo(100, 200);
                path.lineTo(10, 10);
                path.close();
                //outline.setConvexPath(path);

                // return;
            }
            v.setClipToOutline(true);

            if (viewOutlineProvider != null) {
                v.setOutlineProvider(viewOutlineProvider);
            }
            v.invalidate();

            //    RippleDrawable rippleDrawable = (RippleDrawable) v.getBackground();
            //     GradientDrawable rippleBackground = (GradientDrawable) rippleDrawable.getDrawable(0);
            //    rippleBackground.setColor(Color.parseColor("#FF0000"));
            //     rippleDrawable.setColor(ColorStateList.valueOf(Color.WHITE));
            // rippleDrawable.setHotspot(0, 0);

        }
    }

    public static boolean isVersionN() {
        MLog.d(TAG, "codename ----------->" + Build.VERSION.CODENAME);
        return ("N".equals(Build.VERSION.CODENAME));
    }

    public static boolean isVersionMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static boolean isVersionLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }
    public static boolean isVersionKitKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static boolean isVersionMinSupported() {
        return AppRunnerSettings.MIN_SUPPORTED_VERSION > Build.VERSION.SDK_INT;
    }

    public static int calculateColor(float fraction, int startValue, int endValue) {

        int startInt = startValue;
        int startA = (startInt >> 24) & 0xff;
        int startR = (startInt >> 16) & 0xff;
        int startG = (startInt >> 8) & 0xff;
        int startB = startInt & 0xff;

        int endInt = endValue;
        int endA = (endInt >> 24) & 0xff;
        int endR = (endInt >> 16) & 0xff;
        int endG = (endInt >> 8) & 0xff;
        int endB = endInt & 0xff;

        return (startA + (int) (fraction * (endA - startA))) << 24
                | (startR + (int) (fraction * (endR - startR))) << 16
                | (startG + (int) (fraction * (endG - startG))) << 8 | ((startB + (int) (fraction * (endB - startB))));
    }

    public static void debugIntent(String tag, Intent intent) {
        MLog.v(tag, "action: " + intent.getAction());
        MLog.v(tag, "component: " + intent.getComponent());
        Bundle extras = intent.getExtras();
        if (extras != null) {
            for (String key : extras.keySet()) {
                MLog.v(tag, "key [" + key + "]: " + extras.get(key));
            }
        } else {
            MLog.v(tag, "no extras");
        }
    }


    public static String colorHexToHtmlRgba(String colorHex) {
        int c = Color.parseColor(colorHex);
        float alpha = (float) (Color.alpha(c) / 255.0); //html uses normalized values
        int r = Color.red(c);
        int g = Color.green(c);
        int b = Color.blue(c);
        String colorStr = "rgba(" + r + "," + g + "," + b + "," + alpha + ")";

        return colorStr;
    }

    public static void setVolume(Context c, int value) {
        AudioManager audioManager = (AudioManager) c.getSystemService(Context.AUDIO_SERVICE);
        int maxValue = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float val = (float) (value / 100.0 * maxValue);

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, Math.round(val),
                AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
    }


    public static boolean isScreenOn(Context c) {
        PowerManager pm = (PowerManager) c.getSystemService(Context.POWER_SERVICE);
        return pm.isScreenOn();
    }

    // public static void goToSleep(Context c) {
    //PowerManager pm = (PowerManager) c.getSystemService(Context.POWER_SERVICE);
    //pm.goToSleep(100);
    // }

    public static boolean isAirplaneMode(Context c) {
        return Settings.System.getInt(c.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
    }

    public boolean isUSBMassStorageEnabled(Context c) {
        return Settings.System.getInt(c.getContentResolver(), Settings.Global.USB_MASS_STORAGE_ENABLED, 0) != 0;
    }

    public boolean isADBEnabled(Context c) {
        return Settings.System.getInt(c.getContentResolver(), Settings.Global.ADB_ENABLED, 0) != 0;
    }

    public static void setEnableSoundEffects(Context c, boolean b) {
        if (b) {
            Settings.System.putInt(c.getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED, 1);
        } else {
            Settings.System.putInt(c.getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED, 0);

        }
    }

    public static boolean isTablet(Context c) {
        boolean xlarge = ((c.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large = ((c.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }

    public static boolean isWear(Context c) {
        boolean b = false;
        b = c.getResources().getBoolean(R.bool.isWatch);

        return b;
    }

    static PowerManager.WakeLock wl;

    public static void setWakeLock(Context c, boolean b) {

        PowerManager pm = (PowerManager) c.getSystemService(Context.POWER_SERVICE);
        if (wl == null) {
            wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
            if (b) {
                wl.acquire();
            }
        } else {
            if (!b) {
                wl.release();
            }
        }

    }

    public static void setGlobalBrightness(Context c, int brightness) {

        // constrain the value of brightness
        if (brightness < 0) {
            brightness = 0;
        } else if (brightness > 255) {
            brightness = 255;
        }

        ContentResolver cResolver = c.getApplicationContext().getContentResolver();
        Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, brightness);

    }

    public static void setScreenTimeout(Context c, int time) {
        Settings.System.putInt(c.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, time);
    }


    public static void setSpeakerOn(boolean b) {

        Class<?> audioSystemClass;
        try {
            audioSystemClass = Class.forName("android.media.AudioSystem");
            Method setForceUse = audioSystemClass.getMethod("setForceUse", int.class, int.class);
            // First 1 == FOR_MEDIA, second 1 == FORCE_SPEAKER. To go back to
            // the default
            // behavior, use FORCE_NONE (0).
            setForceUse.invoke(null, 1, 1);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    public static String sha1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(text.getBytes(StandardCharsets.ISO_8859_1), 0, text.length());
        byte[] sha1hash = md.digest();
        return convertToHex(sha1hash);
    }

    public static String actionToString(int action) {
        switch (action) {

            case MotionEvent.ACTION_DOWN: return "Down";
            case MotionEvent.ACTION_MOVE: return "Move";
            case MotionEvent.ACTION_POINTER_DOWN: return "Pointer Down";
            case MotionEvent.ACTION_UP: return "Up";
            case MotionEvent.ACTION_POINTER_UP: return "Pointer Up";
            case MotionEvent.ACTION_OUTSIDE: return "Outside";
            case MotionEvent.ACTION_CANCEL: return "Cancel";
        }
        return "";
    }

    public static void prettyPrintMap(Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            MLog.d(entry.getKey() + " : " + entry.getValue());
        }
    }

    // http://stackoverflow.com/questions/4928772/using-color-and-color-darker-in-android

    public static int darkenColor(int color, float factor) {

        int a = Color.alpha( color );
        int r = Color.red( color );
        int g = Color.green( color );
        int b = Color.blue( color );

        return Color.argb( a,
                Math.max( (int)(r * factor), 0 ),
                Math.max( (int)(g * factor), 0 ),
                Math.max( (int)(b * factor), 0 ) );
    }

    public static int lighter(int color, float factor) {
        int red = (int) ((Color.red(color) * (1 - factor) / 255 + factor) * 255);
        int green = (int) ((Color.green(color) * (1 - factor) / 255 + factor) * 255);
        int blue = (int) ((Color.blue(color) * (1 - factor) / 255 + factor) * 255);
        return Color.argb(Color.alpha(color), red, green, blue);
    }
}