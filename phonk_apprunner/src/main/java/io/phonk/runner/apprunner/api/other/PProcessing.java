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

package io.phonk.runner.apprunner.api.other;

import android.annotation.SuppressLint;

import io.phonk.runner.apprunner.AppRunner;
import processing.android.PFragment;
import processing.cardboard.PCardboard;
import processing.core.PApplet;


@SuppressLint("ValidFragment")
public class PProcessing extends PFragment {
    private static final java.lang.String TAG = PProcessing.class.getSimpleName();
    private final PSketch p;

    public interface PInterfaceSettings {
        void settings(PApplet p);
    }

    public interface PInterfaceSetup {
        void setup(PApplet p);
    }

    public interface PInterfaceDraw {
        void draw(PApplet p);
    }

    public PProcessing(AppRunner appRunner) {
        p = new PSketch();
        appRunner.whatIsRunning.add(this);
        setSketch(p);
    }

    public void settings(PInterfaceSettings pIface) {
        p.pfnSettings = pIface;
    }

    public void setup(PInterfaceSetup pIface) {
        p.pfnSetup = pIface;
    }

    public void draw(PInterfaceDraw pIface) {
        p.pfnDraw = pIface;
    }


    public void __stop() {
        p.noLoop();
        p.onPause();
        p.onDestroy();
        p.pfnDraw = null;
    }

}


class PSketch extends PApplet {

    protected PProcessing.PInterfaceDraw pfnDraw;
    protected PProcessing.PInterfaceSetup pfnSetup;
    protected PProcessing.PInterfaceSettings pfnSettings;
    String CARDBOARD_STEREO = PCardboard.STEREO;


    @Override
    public void settings() {
        if (pfnSettings != null) {
            pfnSettings.settings(this);
        }
    }

    @Override
    public void setup() {
        if (pfnSetup != null) {
            pfnSetup.setup(this);
        }
    }

    @Override
    public void draw() {
        if (pfnDraw != null) {
            pfnDraw.draw(this);
        }
    }

}
