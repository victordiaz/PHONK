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

import android.view.View;
import android.view.ViewPropertyAnimator;

public class PAnimation {

    View mView;
    private final ViewPropertyAnimator mAnim;

    public PAnimation(View view) {
        mView = view;
        mAnim = view.animate();
    }

    /*
    public PAnimation x(Object x) {
        int mx = sizeToPixels(x, width);
        x(mx);

        return this;
    }


    public PAnimation xBy(Object x) {
        int mx = sizeToPixels(x, width);
        xBy(mx);

        return this;
    }


    public PAnimation y(Object y) {
        int ym = sizeToPixels(y, height);
        y(my);

        return this;
    }


    public PAnimation yBy(Object y) {
        int ym = sizeToPixels(y, height);
        yBy(my);

        return this;
    }


    public PAnimation z() {

        return this;
    }


    public PAnimation zBy() {

        return this;
    }


    public PAnimation alpha() {

        return this;
    }


    public PAnimation alphaBy() {

        return this;
    }


    public PAnimation rotateX() {

        return this;
    }


    public PAnimation rotateXBy() {

        return this;
    }


    public PAnimation rotateY() {

        return this;
    }


    public PAnimation rotateYBy() {

        return this;
    }


    public PAnimation rotateZ() {

        return this;
    }


    public PAnimation rotateZBy() {

        return this;
    }


    public PAnimation scaleX() {

        return this;
    }


    public PAnimation scaleXBy() {

        return this;
    }


    public PAnimation scaleY() {

        return this;
    }


    public PAnimation scaleYBy() {

        return this;
    }


    public PAnimation duration() {

        return this;
    }


    public PAnimation interpolation() {

        return this;
    }


    public PAnimation onEvent() {

        return this;
    }


    public void cancel() {

    }


    public void start() {

    }
    */


}
