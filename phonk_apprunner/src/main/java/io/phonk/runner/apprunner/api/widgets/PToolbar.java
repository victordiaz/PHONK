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
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import androidx.appcompat.app.ActionBar;

import io.phonk.runner.apprunner.api.ProtoBase;
import io.phonk.runner.apidoc.annotation.ProtoMethod;
import io.phonk.runner.apidoc.annotation.ProtoMethodParam;
import io.phonk.runner.apprunner.AppRunner;

public class PToolbar extends ProtoBase {

    private final AppRunner mAppRunner;
    private final ActionBar mToolbar;
    private int currentColor;
    private Paint paint;

    public PToolbar(AppRunner appRunner, ActionBar toolbar) {
        super(appRunner);
        mAppRunner = appRunner;
        mToolbar = toolbar;
    }

    @ProtoMethod(description = "Set toolbar title name", example = "")
    @ProtoMethodParam(params = {"titleName"})
    public PToolbar title(String title) {
        mToolbar.setTitle(title);
        return this;
    }

    @ProtoMethod(description = "Sets toolbar secondary title", example = "")
    @ProtoMethodParam(params = {"subtitleName"})
    public PToolbar subtitle(String subtitle) {
        mToolbar.setSubtitle(subtitle);
        return this;
    }

    @ProtoMethod(description = "Show/Hide title bar", example = "")
    @ProtoMethodParam(params = {"boolean"})
    public PToolbar show(Boolean b) {
        if (b) {
            mToolbar.show();
        } else {
            mToolbar.hide();
        }
        return this;
    }

    @ProtoMethod(description = "Changes the title bar color", example = "")
    @ProtoMethodParam(params = {"r", "g", "b", "alpha"})
    public PToolbar background(int r, int g, int b, int alpha) {
        int c = Color.argb(alpha, r, g, b);

        ColorDrawable d = new ColorDrawable();
        d.setColor(c);
        mToolbar.setBackgroundDrawable(d);

        return this;
    }

    @ProtoMethod(description = "Changes the title bar color", example = "")
    @ProtoMethodParam(params = {"r", "g", "b", "alpha"})
    public PToolbar background(int r, int g, int b) {
        int c = Color.rgb(r, g, b);

        ColorDrawable d = new ColorDrawable();
        d.setColor(c);
        mToolbar.setBackgroundDrawable(d);

        return this;
    }


//    @ProtoMethod(description = "Changes the title text color", example = "")
//    @ProtoMethodParam(params = {"r", "g", "b", "mContext"})
//    public PToolbar textColor(int r, int g, int b, int alpha) {
//        int c = Color.argb(alpha, r, g, b);
//
//        //int titleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
//        TextView textTitleView = (TextView) mContext.findViewById(titleId);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            mContext.getWindow().setStatusBarColor(c);
//        } else {
//            textTitleView.setTextColor(c);
//        }
//
//        return this;
//    }


    @ProtoMethod(description = "Sets an image rather than text as toolbar title", example = "")
    @ProtoMethodParam(params = {"imageName"})
    public PToolbar icon(String imagePath) {
        Bitmap myBitmap = BitmapFactory.decodeFile(mAppRunner.getProject().getFullPathForFile(imagePath));
        Drawable icon = new BitmapDrawable(mAppRunner.getAppContext().getResources(), myBitmap);

        mToolbar.setIcon(icon);

        return this;
    }

    @Override
    public void __stop() {

    }
}
