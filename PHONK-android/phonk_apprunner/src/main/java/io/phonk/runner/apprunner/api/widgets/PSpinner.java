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

import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Map;

import io.phonk.runner.apidoc.annotation.PhonkClass;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.api.common.ReturnInterface;
import io.phonk.runner.apprunner.api.common.ReturnObject;

@PhonkClass
public class PSpinner extends androidx.appcompat.widget.AppCompatSpinner implements PViewMethodsInterface {
    public final PropertiesProxy props = new PropertiesProxy();
    private String[] mData;

    private int align;

    public PSpinner(AppRunner appRunner) {
        super(appRunner.getAppContext());

        props.onChange((name, value) -> {
            WidgetHelper.applyViewParam(name, value, props, this, appRunner);
            apply(name, value);
        });

        props.put("align", "center");
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
        data_.add("1");
        data_.add("2");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                data_
        );
        // adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        return this;
    }

    @Override
    public void set(float x, float y, float w, float h) {
        props.put("x", x);
        props.put("y", y);
        props.put("w", w);
        props.put("h", h);
    }

    @Override
    public void setProps(Map props) {
        WidgetHelper.setProps(this.props, props);
    }

    @Override
    public Map getProps() {
        return props;
    }

    private void apply(String name, Object value) {
        if (name == null) {
            apply("align");

        } else {
            if (value == null) return;
            switch (name) {
                case "align":
                    switch (value.toString()) {
                        case "left":
                            align = Gravity.LEFT;
                            break;
                        case "center":
                            align = Gravity.CENTER;
                            break;
                        case "right":
                            align = Gravity.RIGHT;
                            break;
                    }
                    setGravity(align);
                    break;
            }
        }
    }

    private void apply(String name) {
        apply(name, props.get(name));
    }


}
