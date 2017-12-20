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
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.protocoderrunner.R;
import io.phonk.runner.apidoc.annotation.ProtoMethod;
import io.phonk.runner.apidoc.annotation.ProtoMethodParam;

public class PCard extends LinearLayout {

    private final int currentColor;
    private final int viewCount = 0;
    private final Context c;
    LinearLayout cardLl;
    TextView title;

    public PCard(Context context) {
        super(context);
        c = context;
        currentColor = Color.argb(255, 255, 255, 255);

        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.pwidget_card, this, true);

        title = (TextView) findViewById(R.id.cardTitle);
        cardLl = (LinearLayout) findViewById(R.id.cardWidgets);

    }

    @Override

    @ProtoMethod(description = "Adds a new view", example = "")
    @ProtoMethodParam(params = {"view"})
    public void addView(View v) {
        v.setAlpha(0);
        v.animate().alpha(1).setDuration(500).setStartDelay(100 * (1 + viewCount));

        // v.setPadding(0, 0, 0, AndroidUtils.pixelsToDp(c, 3));
        cardLl.addView(v);
    }


    @ProtoMethod(description = "Add a row of n columns", example = "")
    @ProtoMethodParam(params = {"columnNumber"})
    public PRow addRow(int n) {
        PRow row = new PRow(c, cardLl, n);

        return row;
    }


    @ProtoMethod(description = "Set the title of the card", example = "")
    @ProtoMethodParam(params = {"text"})
    public void setTitle(String text) {
        if (text.isEmpty() == false) {
            title.setVisibility(View.VISIBLE);
            title.setText(text);
        }
    }


    @ProtoMethod(description = "Changes the title color", example = "")
    @ProtoMethodParam(params = {"colorHex"})
    public void setTitleColor(String color) {
        title.setBackgroundColor(Color.parseColor(color));
    }


    @ProtoMethod(description = "Card with horizontal views", example = "")
    @ProtoMethodParam(params = {""})
    public void setHorizontal() {
        LinearLayout ll = (LinearLayout) findViewById(R.id.cardWidgets);
        ll.setOrientation(LinearLayout.HORIZONTAL);
    }


    @ProtoMethod(description = "Card with vertical views", example = "")
    @ProtoMethodParam(params = {""})
    public void setVertical() {
        LinearLayout ll = (LinearLayout) findViewById(R.id.cardWidgets);
        ll.setOrientation(LinearLayout.VERTICAL);
    }

}
