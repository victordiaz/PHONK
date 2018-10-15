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

import android.view.SurfaceView;

import io.phonk.runner.apprunner.AppRunner;


public class PCanvas2 extends SurfaceView {

    private static final String TAG = PCanvas2.class.getSimpleName();

    protected final AppRunner mAppRunner;
    public interface OnSetupCallback { void event (PCanvas2 c); }
    public interface OnDrawCallback { void event (PCanvas2 c); }
    public OnSetupCallback setup;
    public OnDrawCallback draw;


    public PCanvas2(AppRunner appRunner, int w, int h) {
        this(appRunner);
    }

    public PCanvas2(AppRunner appRunner) {
        super(appRunner.getAppContext());
        mAppRunner = appRunner;
  }


}
