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
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by victormanueldiazbarrales on 29/07/14.
 */
public class PGridRow extends LinearLayout {

    public PGridRow(Context context, int cols) {
        super(context);

        setOrientation(LinearLayout.HORIZONTAL);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        this.setWeightSum(cols);
    }

    public PGridRow addViewInRow(View v) {
        LinearLayout.LayoutParams lParams = (LinearLayout.LayoutParams) getLayoutParams(); //or create new LayoutParams...

        if (v.getClass().equals(PSlider.class)) {
            lParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        } else {
            lParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        }
        lParams.weight = 1;
        lParams.width = ViewGroup.LayoutParams.MATCH_PARENT;

        v.setLayoutParams(lParams);
        addView(v);

        return this;
    }
}
