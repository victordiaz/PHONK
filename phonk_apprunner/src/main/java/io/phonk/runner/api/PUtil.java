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

package io.phonk.runner.api;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.FaceDetector;
import android.os.Handler;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import io.phonk.runner.api.common.ReturnObject;
import io.phonk.runner.api.other.PLooper;
import io.phonk.runner.api.other.SignalUtils;
import io.phonk.runner.apidoc.annotation.ProtoMethod;
import io.phonk.runner.apidoc.annotation.ProtoMethodParam;
import io.phonk.runner.apidoc.annotation.ProtoObject;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.base.utils.MLog;
import io.phonk.runner.base.views.CanvasUtils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

@ProtoObject
public class PUtil extends ProtoBase {

    private final Handler handler;
    ArrayList<Runnable> rl = new ArrayList<Runnable>();

    public PUtil(AppRunner appRunner) {
        super(appRunner);
        handler = new Handler();
    }

    // --------- getRequest ---------//
    interface getRequestCB {
        void event(int eventType, String responseString);
    }

    public void setAnObject(NativeObject obj) {

        for (Map.Entry<Object, Object> entry : obj.entrySet()) {
            String key = (String) entry.getKey();
            Object o = entry.getValue();

            MLog.d(TAG, "setAnObject -> " + key + " " + o);
        }

        MLog.d(TAG, "q --> " + obj.get("q"));
    }

    public ReturnObject getAnObject() {

        // HashMap map = new HashMap();

        ReturnObject ret = new ReturnObject();
        ret.put("qq", 1);
        ret.put("qq 2", 2);

        /*
        NativeObject ret = (NativeObject) getAppRunner().interp.newNativeObject();
        ret.defineProperty("q", 2, NativeObject.READONLY);

        ReturnObject ret1 = new ReturnObject();
        ret1.put();

        */

        return ret;
    }

    /*
     * 1. get arraylist to native array
     * 2. set native array to arraylist
     */
    public NativeArray getAnArray() {
        ArrayList array = new ArrayList();
        array.add("1");
        array.add("2");

        NativeArray ret = (NativeArray) getAppRunner().interp.newNativeArrayFrom(array.toArray());

        return ret;
    }

    public void setAnArray(NativeArray array) {
        for (int i = 0; i < array.size(); i++) {
            MLog.d(TAG, "setArrayList -> " + array.get(i));
        }
    }

    public String getCharFromUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }

    @ProtoMethod(description = "Creates a looper that loops a given function every 'n' milliseconds", example = "")
    @ProtoMethodParam(params = {"milliseconds", "function()"})
    public PLooper loop(final int duration, final PLooper.LooperCB callbackkfn) {
        return new PLooper(getAppRunner(), duration, callbackkfn);
    }

    @ProtoMethod(description = "Creates a looper that loops a given function every 'n' milliseconds", example = "")
    @ProtoMethodParam(params = {"milliseconds"})
    public PLooper loop(final int duration) {
        return new PLooper(getAppRunner(), duration, null);
    }

    // --------- delay ---------//
    public interface delayCB {
        void event();
    }

    @ProtoMethod(description = "Delay a given function 'n' milliseconds", example = "")
    @ProtoMethodParam(params = {"milliseconds", "function()"})
    public void delay(final int duration, final delayCB fn) {

        Runnable task = new Runnable() {
            @Override
            public void run() {
                // handler.postDelayed(this, duration);
                fn.event();
                handler.removeCallbacks(this);
                rl.remove(this);
            }
        };
        handler.postDelayed(task, duration);

        rl.add(task);
    }


    @ProtoMethod(description = "Stop all timers", example = "")
    @ProtoMethodParam(params = {""})
    public void stopAllTimers() {
        Iterator<Runnable> ir = rl.iterator();
        while (ir.hasNext()) {
            handler.removeCallbacks(ir.next());
            // handler.post(ir.next());
        }
    }

    // http://stackoverflow.com/questions/4605527/converting-pixels-to-dp

    @ProtoMethod(description = "Convert given dp to pixels", example = "")
    @ProtoMethodParam(params = {""})
    public float dpToPixels(float dp) {
        Resources resources = getContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }


    @ProtoMethod(description = "Convert given px to dp", example = "")
    @ProtoMethodParam(params = {""})
    public float pixelsToDp(float px) {
        Resources resources = getContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }


    @ProtoMethod(description = "Convert given mm to pixels", example = "")
    @ProtoMethodParam(params = {""})
    public float mmToPixels(float mm) {
        float px = TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_MM, mm, getContext().getResources().getDisplayMetrics());
        return px;
    }


    @ProtoMethod(description = "Convert given pixels to mm", example = "")
    @ProtoMethodParam(params = {""})
    public float pixelsToMm(int px) {
        float onepx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, 1, getContext().getResources()
                .getDisplayMetrics());

        return px * onepx;
    }

    public interface AnimCB {
        void event(float data);
    }


    //TODO include the new support lib v22 interpolations
    @ProtoMethod(description = "Animate a variable from min to max in a specified time using 'bounce', 'linear', 'decelerate', 'anticipate', 'aovershoot', 'accelerate' type  ", example = "")
    @ProtoMethodParam(params = {"type", "min", "max", "time", "function(val)"})
    public ValueAnimator anim(String type, float min, float max, int time, final AnimCB callback) {
        TimeInterpolator interpolator = null;
        if (type.equals("bounce")) {
            interpolator = new BounceInterpolator();
        } else if (type.equals("linear")) {
            interpolator = new LinearInterpolator();
        } else if (type.equals("decelerate")) {
            interpolator = new DecelerateInterpolator();
        } else if (type.equals("anticipate")) {
            interpolator = new AnticipateInterpolator();
        } else if (type.equals("aovershoot")) {
            interpolator = new AnticipateOvershootInterpolator();
        } else {
            interpolator = new AccelerateDecelerateInterpolator();
        }

        ValueAnimator va = ValueAnimator.ofFloat(min, max);
        va.setDuration(time);
        va.setInterpolator(interpolator);

        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Float value = (Float) animation.getAnimatedValue();
                callback.event(value);
                MLog.d(TAG, "val " + value + " " + animation.getAnimatedValue());
            }
        });

        return va;
    }


    @ProtoMethod(description = "Parse a color and return and int representing it", example = "")
    @ProtoMethodParam(params = {"colorString"})
    public int parseColor(String c) {
        return Color.parseColor(c);
    }



    @ProtoMethod(description = "Detect faces in a bitmap", example = "")
    @ProtoMethodParam(params = {"Bitmap", "numFaces"})
    public int detectFaces(Bitmap bmp, int num_faces) {
        FaceDetector face_detector = new FaceDetector(bmp.getWidth(), bmp.getHeight(), num_faces);
        FaceDetector.Face[] faces = new FaceDetector.Face[num_faces];
        int face_count = face_detector.findFaces(bmp, faces);

        return face_count;
    }

    public String bitmapToBase64String(Bitmap bmp) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();

        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

        return encoded;
    }

    @ProtoMethod(description = "Converts byte array to bmp", example = "")
    @ProtoMethodParam(params = {"encodedImage"})
    public Bitmap base64StringToBitmap(String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);

        // MLog.d(TAG, "bytes--> " + decodedString);
        BitmapFactory.Options bitmap_options = new BitmapFactory.Options();
        bitmap_options.inPreferredConfig = Bitmap.Config.RGB_565;

        final Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, bitmap_options);

        // MLog.d(TAG, "bitmap --> " + bitmap);

        return bitmap;
    }

    public float map (float val, float istart, float istop, float ostart, float ostop) {
        return CanvasUtils.map(val, istart, istop, ostart, ostop);
    }

    public SignalUtils signal(int n) {
        return new SignalUtils(getAppRunner(), n);
    }


    @Override
    public void __stop() {
        stopAllTimers();
    }
}