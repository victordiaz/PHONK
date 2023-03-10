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

import android.graphics.Bitmap;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Map;

import io.phonk.runner.apidoc.annotation.PhonkClass;
import io.phonk.runner.apidoc.annotation.PhonkMethod;
import io.phonk.runner.apidoc.annotation.PhonkMethodParam;
import io.phonk.runner.apprunner.AppRunner;

@PhonkClass
public class PImage extends androidx.appcompat.widget.AppCompatImageView implements PViewMethodsInterface {
    private static final String TAG = PImage.class.getSimpleName();
    public final StylePropertiesProxy props = new StylePropertiesProxy();
    protected final AppRunner mAppRunner;
    protected final ImageStyler styler;

    public PImage(AppRunner appRunner, Map initProps) {
        super(appRunner.getAppContext());
        this.mAppRunner = appRunner;

        styler = new ImageStyler(appRunner, this, props);
        props.eventOnChange = false;
        props.put("background", props, "#00FFFFFF");
        props.put("srcMode", props, "fit");

        addFromChild(props);

        Styler.fromTo(initProps, props);
        props.eventOnChange = true;
        styler.apply();
    }

    protected void addFromChild(StylePropertiesProxy props) {
    }

    @PhonkMethod(description = "Sets an image", example = "")
    @PhonkMethodParam(params = {"imageName"})
    public PImage load(String imagePath, int width, int height) {
        if (imagePath.startsWith("http://")) {
            Picasso.with(mAppRunner.getAppContext()).load(imagePath).resize(width, height).centerCrop().into(this);
        } else {
            imagePath = mAppRunner.getProject().getFullPathForFile(imagePath);
            Picasso.with(mAppRunner.getAppContext()).load(new File(imagePath)).resize(width, height).centerCrop().into(
                    this);
        }

        return this;
    }

    public PImage load(String imagePath) {
        if (imagePath.startsWith("http://")) {
            Picasso.with(mAppRunner.getAppContext()).load(imagePath).into(this);
        } else {
            imagePath = mAppRunner.getProject().getFullPathForFile(imagePath);
            Picasso.with(mAppRunner.getAppContext()).load(new File(imagePath)).into(this);
        }

        return this;
    }

    public PImage load(Bitmap bmp) {
        super.setImageBitmap(bmp);

        return this;
    }

    public PImage mode(String mode) {
        switch (mode) {
            case "tiled":
                BitmapDrawable bitmapDrawable = ((BitmapDrawable) this.getDrawable());

                Shader.TileMode tileMode = Shader.TileMode.REPEAT;
                bitmapDrawable.setTileModeXY(tileMode, tileMode);

                setBackground(bitmapDrawable);
                setImageBitmap(null);
                //setScaleX(2);
                break;

            case "fit":
                this.setScaleType(ScaleType.FIT_CENTER);
                break;

            case "crop":
                this.setScaleType(ScaleType.CENTER_CROP);
                break;

            case "resize":
                this.setScaleType(ScaleType.FIT_XY);
                break;
        }
        return this;
    }

    @Override
    public void set(float x, float y, float w, float h) {
        styler.setLayoutProps(x, y, w, h);
    }

    class ImageStyler extends Styler {
        ImageStyler(AppRunner appRunner, View view, StylePropertiesProxy props) {
            super(appRunner, view, props);
        }

        @Override
        public void apply() {
            super.apply();

            mode(mProps.get("srcMode").toString());
        }
    }

    @Override
    public void setProps(Map style) {
        styler.setProps(style);
    }

    @Override
    public Map getProps() {
        return props;
    }

    @Override
    public int id() {
        return getId();
    }


}
