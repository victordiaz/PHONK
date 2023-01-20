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

import android.view.MotionEvent;
import android.view.View;

import io.phonk.runner.apprunner.api.common.ReturnInterface;
import io.phonk.runner.apprunner.api.common.ReturnObject;

public class WidgetHelper {

    public static void setMovable(View viewHandler, final View viewContainer, final ReturnInterface callback) {
        View.OnTouchListener onMoveListener = new View.OnTouchListener() {

            public int x_init;
            public int y_init;

            @Override
            public boolean onTouch(View v, MotionEvent e) {
                int action = e.getActionMasked();

                switch (action) {

                    case MotionEvent.ACTION_DOWN:
                        x_init = (int) e.getRawX() - (int) viewContainer.getX();
                        //MLog.network(getContext(), TAG, "" + x_init + " " + (int) e.getRawX() + " " + (int) mWindow
                        // .getX() + " " + (int) mWindow.getLeft());
                        y_init = (int) e.getRawY() - (int) viewContainer.getY();

                        break;

                    case MotionEvent.ACTION_MOVE:

                        int x_cord = (int) e.getRawX();
                        int y_cord = (int) e.getRawY();

                        int posX = x_cord - x_init;
                        int posY = y_cord - y_init;

                        viewContainer.setX(posX);
                        viewContainer.setY(posY);

                        ReturnObject r = new ReturnObject();
                        r.put("x", posX);
                        r.put("y", posY);

                        if (callback != null) callback.event(r);

                        break;
                }

                return true;
            }

        };

        viewHandler.setOnTouchListener(onMoveListener);
    }

    public static void removeMovable(View viewHandler) {
        viewHandler.setOnTouchListener(null);
    }
}
