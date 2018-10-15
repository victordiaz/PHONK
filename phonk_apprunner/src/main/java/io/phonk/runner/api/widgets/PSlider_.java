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

import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LayerDrawable;
import android.view.Gravity;
import android.widget.SeekBar;

import io.phonk.runner.api.common.ReturnInterface;
import io.phonk.runner.api.common.ReturnObject;
import io.phonk.runner.apidoc.annotation.ProtoMethod;
import io.phonk.runner.apidoc.annotation.ProtoMethodParam;
import io.phonk.runner.apprunner.AppRunner;

public class PSlider_ extends SeekBar {

    private final PSlider_ mSlider;
    private float mMin = 0.0f;
    private float mMax = 100f;
    private float mCurrentValue = 0.0f;
    private int MAX_VALUE = 999999999;
    private Styler style;

    public PSlider_(AppRunner appRunner) {
        super(appRunner.getAppContext());
        super.setMax(MAX_VALUE);
        // setProgressDrawable(getResources().getDrawable(R.drawable.ui_seekbar_progress));

        mSlider = this;
        // getStyles = new Styler(appRunner, this);

        //Custom background drawable allows you to draw how you want it to look if needed
        // SeekBarBackgroundDrawable backgroundDrawable = new SeekBarBackgroundDrawable(appRunner.getAppContext());
        ColorDrawable progressDrawable = new ColorDrawable(Color.BLUE);
        SeekBarProgressDrawable clipProgressDrawable = new SeekBarProgressDrawable(progressDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL, appRunner.getAppContext());

        ColorDrawable progressDrawable2 = new ColorDrawable(Color.GREEN);
        //Custom seek bar slider drawable. Also allows you to modify appearance.
        InsetDrawable insetDrawable = new InsetDrawable(progressDrawable2, 0, 0, 0, 0);

        Drawable[] drawables = new Drawable[]{ insetDrawable, clipProgressDrawable };

        //Create layer drawables with android pre-defined ids
        LayerDrawable layerDrawable = new LayerDrawable(drawables);
        // layerDrawable.setId(0,android.R.id.background);
        // layerDrawable.setId(1,android.R.id.slider);

        //Set to seek bar
        setThumb(null);
        setProgressDrawable(layerDrawable);
    }

    /*
    public void setSeekBarImages(){
        Drawable drawable = new BitmapDrawable(appState.images.get(Integer.parseInt(image_a)));
        ClipDrawable clip = new ClipDrawable(drawable, Gravity.LEFT,ClipDrawable.HORIZONTAL);

        Drawable drawable2 = new BitmapDrawable(appState.images.get(Integer.parseInt(image)));
        InsetDrawable d1= new InsetDrawable(drawable2,5,5,5,5);
        //the padding u want to use
        setThumb(null);
        LayerDrawable mylayer = new LayerDrawable(new Drawable[]{d1,clip});
        setProgressDrawable(mylayer);
        setProgress(0);
    }
    */

    @ProtoMethod(description = "Changes slider value", example = "")
    @ProtoMethodParam(params = {"value"})
    public void value(float value) {
        mCurrentValue = value;
        int valueInt = (int) ((value - mMin) / (mMax - mMin) * MAX_VALUE);
        setProgress(valueInt);
    }

    @ProtoMethod(description = "Gets the slider value", example = "")
    @ProtoMethodParam(params = {""})
    public float value() {
        return mCurrentValue;
    }

    @ProtoMethod(description = "Sets the minimum and maximum slider values", example = "")
    @ProtoMethodParam(params = {""})
    public PSlider_ range(float min, float max) {
        mMin = min;
        mMax = max;
        return this;
    }

    @ProtoMethod(description = "Sets the minimum slider value", example = "")
    @ProtoMethodParam(params = {""})
    public PSlider_ min(float min) {
        mMin = min;
        return this;
    }

    @ProtoMethod(description = "Sets the maximum slider value", example = "")
    @ProtoMethodParam(params = {""})
    public PSlider_ max(float max) {
        mMax = max;
        return this;
    }

    @ProtoMethod(description = "Gets the minimum  slider value", example = "")
    @ProtoMethodParam(params = {""})
    public float min() {
        return mMin;
    }

    @ProtoMethod(description = "Gets the maximum slider value", example = "")
    @ProtoMethodParam(params = {""})
    public float max() {
        return mMax;
    }

    private float valueToFloat(int valueInt) {
        float valueFloat = (float) (valueInt * (mMax - mMin) / MAX_VALUE) + mMin;
        return valueFloat;
    }

    @ProtoMethod(description = "On slider change", example = "")
    @ProtoMethodParam(params = {"function(value)"})
    public PSlider_ onChange(final ReturnInterface callbackfn) {
        // Add the change listener
        mSlider.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ReturnObject r = new ReturnObject(PSlider_.this);
                r.put("value", mSlider.valueToFloat(progress));
                callbackfn.event(r);
            }
        });

        return mSlider;
    }

}
