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

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

public class TextUtils {
    public static void changeFont(Context c, int id, View parentView, String fontName) {
        TextView txt = (TextView) parentView.findViewById(id);

        // Typeface font = Typeface.createFromAsset(c.getAssets(),
        // "brownproregular.otf");
        Typeface font = Typeface.createFromAsset(c.getAssets(), fontName);
        txt.setTypeface(font);
    }

    public static void changeFont(Context c, View txt, String fontName) {

        // Typeface font = Typeface.createFromAsset(c.getAssets(),
        // "brownproregular.otf");
        Typeface font = Typeface.createFromAsset(c.getAssets(), fontName);
        ((TextView) txt).setTypeface(font);
    }

    public static void changeFont(Activity activity, int id, String fontName) {
        TextView txt = (TextView) activity.findViewById(id);

        // Typeface font = Typeface.createFromAsset(c.getAssets(),
        // "brownproregular.otf");
        Typeface font = Typeface.createFromAsset(activity.getAssets(), fontName);
        txt.setTypeface(font);
    }

    public static void changeFont(Context c, TextView txt, String fontName) {

        Typeface font = Typeface.createFromAsset(c.getAssets(), fontName);
        txt.setTypeface(font);
    }

}