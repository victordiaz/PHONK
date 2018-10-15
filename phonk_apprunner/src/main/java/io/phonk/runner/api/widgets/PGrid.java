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
import android.widget.LinearLayout;

import org.mozilla.javascript.NativeArray;
import io.phonk.runner.apidoc.annotation.ProtoMethod;
import io.phonk.runner.apidoc.annotation.ProtoMethodParam;

/**
 * Created by victormanueldiazbarrales on 28/07/14.
 */
public class PGrid extends LinearLayout {
    private final Context context;
    private int columns = 1;

    public PGrid(Context context) {
        super(context);
        this.context = context;
        setOrientation(LinearLayout.VERTICAL);


    }


    @ProtoMethod(description = "Adds a new row with n columns", example = "")
    @ProtoMethodParam(params = {"numColumns"})
    public PGridRow addRow(int cols) {
        PGridRow ll2 = new PGridRow(context, cols);
        this.addView(ll2);

        return ll2;
    }


    @ProtoMethod(description = "Specify the number of columns", example = "")
    @ProtoMethodParam(params = {"colums"})
    public PGrid columns(int cols) {
        this.columns = cols;

        return this;
    }

    //TODO Placeholder
    //
    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {""})
    public PGrid inPlace(int x, int y, int w, int h) {

        return this;
    }

    //TODO Placeholder

    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {""})
    public PGrid using(NativeArray array) {

        return this;
    }

    //TODO placeholder

    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {""})
    public PGrid build() {

        return this;
    }
}
