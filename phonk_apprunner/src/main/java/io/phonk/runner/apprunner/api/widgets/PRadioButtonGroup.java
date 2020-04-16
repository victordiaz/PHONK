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
import android.widget.RadioGroup;

import io.phonk.runner.apprunner.api.common.ReturnInterface;
import io.phonk.runner.apprunner.api.common.ReturnObject;

public class PRadioButtonGroup extends RadioGroup {

    public PRadioButtonGroup(Context context) {
        super(context);
        orientation("vertical");
    }

    public void orientation(String orientation) {
        int o = RadioGroup.HORIZONTAL;
        switch (orientation) {
            case "horizontal":
                o = RadioGroup.VERTICAL;
                break;
            case "vertical":
                o = RadioGroup.VERTICAL;
                break;
        }
        setOrientation(o);
    }

    public PRadioButton add(String text) {
        PRadioButton rb = new PRadioButton(getContext());
        rb.selected(false);
        rb.text(text);
        addView(rb);
        return rb;
    }

    public void clear() {
        clearCheck();
    }

    public void onSelected(final ReturnInterface cb) {
        this.setOnCheckedChangeListener((group, checkedId) -> {
            ReturnObject r = new ReturnObject(PRadioButtonGroup.this);
            PRadioButton rb = findViewById(checkedId);
            r.put("selected", rb.getText());
            cb.event(r);
        });
    }
}
