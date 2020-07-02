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
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Map;

import io.phonk.runner.apidoc.annotation.PhonkClass;
import io.phonk.runner.apidoc.annotation.PhonkMethod;
import io.phonk.runner.apidoc.annotation.PhonkMethodParam;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.api.common.ReturnInterface;
import io.phonk.runner.apprunner.api.common.ReturnObject;

@PhonkClass
public class PSpinner extends androidx.appcompat.widget.AppCompatSpinner implements PViewMethodsInterface, PTextInterface {
    private String[] mData;

    public StyleProperties props = new StyleProperties();
    // public Styler styler;
    private Typeface mFont;

    public PSpinner(AppRunner appRunner) {
        super(appRunner.getAppContext());
        // styler = new Styler(appRunner, this, props);
        // styler.apply();
        // setTypeface(mFont);
    }

    public PSpinner onSelected(final ReturnInterface callback) {
        this.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ReturnObject r = new ReturnObject(PSpinner.this);
                r.put("selected", mData[position]);
                r.put("selectedId", position);
                callback.event(r);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return this;
    }

    public PSpinner setData(String[] data) {
        this.mData = data;

        ArrayList<String> data_ = new ArrayList<>();
        data_.add("qq1");
        data_.add("qq2");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, data_);
        // adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        return this;
    }


    @Override
    public View textFont(Typeface font) {
        mFont = font;

        // this.setTypeface(font);
        return this;
    }

    @Override
    public View textSize(int size) {
        // this.setTextSize(size);
        return this;
    }

    @Override
    public View textColor(String textColor) {
        // this.setTextColor(Color.parseColor(textColor));
        return this;
    }

    @Override
    @PhonkMethod(description = "Changes the font text color", example = "")
    @PhonkMethodParam(params = {"colorHex"})
    public View textColor(int c) {
        // this.setTextColor(c);
        return this;
    }

    @Override
    public View textSize(float textSize) {
        // this.setTextSize(textSize);
        return this;
    }

    @Override
    public View textStyle(int style) {
        // this.setTypeface(null, style);
        return this;
    }

    @Override
    public View textAlign(int alignment) {
        setGravity(alignment);
        return this;
    }

    @Override
    public void set(float x, float y, float w, float h) {
        // styler.setLayoutProps(x, y, w, h);
    }

    @Override
    public void setProps(Map style) {
        // styler.setStyle(style);
    }

    @Override
    public Map getProps() {
        return props;
    }


}
