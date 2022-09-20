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

package io.phonk.runner.base.gui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import io.phonk.runner.R;
import io.phonk.runner.base.BaseFragment;

public class AbsolutePositionFragment extends BaseFragment {

    private View v;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        v = new RelativeLayout(getActivity());
        v.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT));
        // ac.setContentView(mainLayout);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        v = inflater.inflate(R.layout.fragment_empty, container, false);
        addSeekBar(10, 10, 100, 20, 100);

        return v;

    }

    private void addSeekBar(int x, int y, int w, int h, int max) {
        SeekBar sb = new SeekBar(getActivity());

        sb.setMax(max);
        sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }
        });

        placeView(sb, x, y, w, h);
    }

    private void placeView(View v, int x, int y, int w, int h) {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(1, 1);

        layoutParams.leftMargin = x;
        layoutParams.topMargin = y;
        layoutParams.width = w;
        layoutParams.height = h;

        v.setLayoutParams(layoutParams);
    }

    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

}
