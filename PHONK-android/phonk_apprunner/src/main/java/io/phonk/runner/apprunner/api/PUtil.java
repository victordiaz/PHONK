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

package io.phonk.runner.apprunner.api;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.FaceDetector;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.welie.blessed.BluetoothBytesParser;

import java.io.ByteArrayOutputStream;

import io.phonk.runner.apidoc.annotation.PhonkMethod;
import io.phonk.runner.apidoc.annotation.PhonkMethodParam;
import io.phonk.runner.apidoc.annotation.PhonkObject;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.api.other.PDelay;
import io.phonk.runner.apprunner.api.other.PLooper;
import io.phonk.runner.apprunner.api.other.SignalUtils;
import io.phonk.runner.base.utils.AndroidUtils;
import io.phonk.runner.base.utils.MLog;
import io.phonk.runner.base.views.CanvasUtils;

@PhonkObject
public class PUtil extends ProtoBase {

    public PUtil(AppRunner appRunner) {
        super(appRunner);
    }

    public String getCharFromUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }

    @PhonkMethod(description = "Creates a looper that loops a given function every 'n' milliseconds", example = "")
    @PhonkMethodParam(params = {"milliseconds", "function()"})
    public PLooper loop(final int duration, final PLooper.LooperCB callbackkfn) {
        return new PLooper(getAppRunner(), duration, callbackkfn);
    }

    @PhonkMethod(description = "Creates a looper that loops a given function every 'n' milliseconds", example = "")
    @PhonkMethodParam(params = {"milliseconds"})
    public PLooper loop(final int duration) {
        return new PLooper(getAppRunner(), duration, null);
    }

    @PhonkMethod(description = "Delay a given function 'n' milliseconds", example = "")
    @PhonkMethodParam(params = {"milliseconds", "function()"})
    public PDelay delay(final int delay, final PDelay.DelayCB fn) {
        return new PDelay(getAppRunner(), delay, fn);
    }

    // http://stackoverflow.com/questions/4605527/converting-pixels-to-dp
    @PhonkMethod(description = "Convert given dp to pixels", example = "")
    @PhonkMethodParam(params = {""})
    public float dpToPixels(float dp) {
        Resources resources = getContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }

    @PhonkMethod(description = "Convert given px to dp", example = "")
    @PhonkMethodParam(params = {""})
    public float pixelsToDp(float px) {
        Resources resources = getContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return px / (metrics.densityDpi / 160f);
    }

    @PhonkMethod(description = "Convert given mm to pixels", example = "")
    @PhonkMethodParam(params = {""})
    public float mmToPixels(float mm) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_MM,
                mm,
                getContext().getResources().getDisplayMetrics()
        );
    }


    @PhonkMethod(description = "Convert given pixels to mm", example = "")
    @PhonkMethodParam(params = {""})
    public float pixelsToMm(int px) {
        float onepx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_MM,
                1,
                getContext().getResources().getDisplayMetrics()
        );

        return px * onepx;
    }

    @PhonkMethod
    public Object parseBytes(byte[] bytes, String type) {
        BluetoothBytesParser parser = new BluetoothBytesParser(bytes);

        switch (type) {
            case "string":
                return parser.getStringValue(0);
            case "float":
                return parser.getFloatValue(BluetoothBytesParser.FORMAT_FLOAT);
            case "sfloat":
                return parser.getFloatValue(BluetoothBytesParser.FORMAT_SFLOAT);
            case "long":
                return parser.getLongValue();
            case "sint8":
                return parser.getIntValue(BluetoothBytesParser.FORMAT_SINT8);
            case "sint16":
                return parser.getIntValue(BluetoothBytesParser.FORMAT_SINT16);
            case "sint32":
                return parser.getIntValue(BluetoothBytesParser.FORMAT_SINT32);
            case "uint8":
                return parser.getIntValue(BluetoothBytesParser.FORMAT_UINT8);
            case "uint16":
                parser.getIntValue(BluetoothBytesParser.FORMAT_UINT16);
            case "uint32":
                return parser.getIntValue(BluetoothBytesParser.FORMAT_UINT32);
            case "date":
                return parser.getDateTime();
            default:
                return null;
        }
    }

    //TODO include the new support lib v22 interpolations
    @PhonkMethod(description = "Animate a variable from min to max in a specified time using 'bounce', 'linear', " +
            "'decelerate', 'anticipate', 'aovershoot', 'accelerate' type  ", example = "")
    @PhonkMethodParam(params = {"type", "min", "max", "time", "function(val)"})
    public ValueAnimator anim(String type, float min, float max, int time, final AnimCB callback) {
        TimeInterpolator interpolator = null;
        switch (type) {
            case "bounce":
                interpolator = new BounceInterpolator();
                break;
            case "linear":
                interpolator = new LinearInterpolator();
                break;
            case "decelerate":
                interpolator = new DecelerateInterpolator();
                break;
            case "anticipate":
                interpolator = new AnticipateInterpolator();
                break;
            case "aovershoot":
                interpolator = new AnticipateOvershootInterpolator();
                break;
            default:
                interpolator = new AccelerateDecelerateInterpolator();
                break;
        }

        ValueAnimator va = ValueAnimator.ofFloat(min, max);
        va.setDuration(time);
        va.setInterpolator(interpolator);

        va.addUpdateListener(animation -> {
            Float value = (Float) animation.getAnimatedValue();
            callback.event(value);
            MLog.d(TAG, "val " + value + " " + animation.getAnimatedValue());
        });

        return va;
    }

    @PhonkMethod(description = "Parse a color and return and int representing it", example = "")
    @PhonkMethodParam(params = {"colorString"})
    public int parseColor(String c) {
        return Color.parseColor(c);
    }

    @PhonkMethod(description = "Detect faces in a bitmap", example = "")
    @PhonkMethodParam(params = {"Bitmap", "numFaces"})
    public int detectFaces(Bitmap bmp, int num_faces) {
        FaceDetector face_detector = new FaceDetector(bmp.getWidth(), bmp.getHeight(), num_faces);
        FaceDetector.Face[] faces = new FaceDetector.Face[num_faces];

        return face_detector.findFaces(bmp, faces);
    }

    public String stringToBase64(String string) {
        return Base64.encodeToString(string.getBytes(), Base64.DEFAULT);
    }

    public String base64ToString(String base64) {
        return new String(Base64.decode(base64, Base64.DEFAULT));
    }

    public String bitmapToBase64(Bitmap bmp, int quality) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, quality, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    @PhonkMethod(description = "Converts byte array to bmp", example = "")
    @PhonkMethodParam(params = {"encodedImage"})
    public Bitmap base64ToBitmap(String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);

        // MLog.d(TAG, "bytes--> " + decodedString);
        BitmapFactory.Options bitmap_options = new BitmapFactory.Options();
        bitmap_options.inPreferredConfig = Bitmap.Config.RGB_565;

        // MLog.d(TAG, "bitmap --> " + bitmap);

        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, bitmap_options);
    }

    public float map(float val, float istart, float istop, float ostart, float ostop) {
        return CanvasUtils.map(val, istart, istop, ostart, ostop);
    }

    public SignalUtils signal(int n) {
        return new SignalUtils(getAppRunner(), n);
    }

    public int sizeToPixels(Object val, int toValue) {
        int returnVal = -1;

        if (val instanceof String) {
            // MLog.d(TAG, "oo is string");
            String str = (String) val;
            String[] splitted = str.split("(?<=\\d)(?=[a-zA-Z%])|(?<=[a-zA-Z%])(?=\\d)");

            double value = Double.parseDouble(splitted[0]);
            String type = splitted[1];

            returnVal = transform(type, value, toValue);
        } else if (val instanceof Double) {
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
                retValue = AndroidUtils.dpToPixels(getAppRunner().getAppContext(), value.intValue());
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
                retValue = (int) (value * getAppRunner().pUi.screenWidth);

                break;
            case "h":
                retValue = (int) (value * getAppRunner().pUi.screenHeight);
                break;
            default:
                break;
        }

        // MLog.d(TAG, "oo --> " + retValue);
        return retValue;
    }

    @Override
    public void __stop() {

    }

    public interface AnimCB {
        void event(float data);
    }
}