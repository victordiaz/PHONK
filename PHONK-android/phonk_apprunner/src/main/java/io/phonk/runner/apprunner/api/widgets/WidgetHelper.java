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

import java.util.Map;

import io.phonk.runner.apprunner.AppRunner;
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

    private static void applyViewParam(String name, PropertiesProxy props, View view, AppRunner appRunner) {
        applyViewParam(name, props.get(name), props, view, appRunner);
    }

    public static void applyViewParam(String name, Object value, PropertiesProxy props, View view, AppRunner appRunner) {
        if (name == null) {
            applyViewParam("x", props, view, appRunner);
            applyViewParam("y", props, view, appRunner);
            applyViewParam("w", props, view, appRunner);
            applyViewParam("h", props, view, appRunner);
            applyViewParam("enabled", props, view, appRunner);
            applyViewParam("visibility", props, view, appRunner);

        } else {
            if (value == null) return;
            switch (name) {
                case "x":
                    if (view.getLayoutParams() instanceof FixedLayout.LayoutParams) {
                        ((FixedLayout.LayoutParams) view.getLayoutParams()).x = appRunner.pUtil.sizeToPixels(value, appRunner.pUi.screenWidth);
                    }
                    break;

                case "y":
                    if (view.getLayoutParams() instanceof FixedLayout.LayoutParams) {
                        ((FixedLayout.LayoutParams) view.getLayoutParams()).y = appRunner.pUtil.sizeToPixels(value, appRunner.pUi.screenHeight);
                    }
                    break;

                case "w":
                    if (view.getLayoutParams() != null) {
                        view.getLayoutParams().width = appRunner.pUtil.sizeToPixels(value, appRunner.pUi.screenWidth);
                    }
                    break;

                case "h":
                    if (view.getLayoutParams() != null) {
                        view.getLayoutParams().height = appRunner.pUtil.sizeToPixels(value, appRunner.pUi.screenHeight);
                    }
                    break;

                case "enabled":
                    view.setEnabled(value instanceof Boolean ? (Boolean) value : false);
                    break;

                case "visibility":
                    view.setVisibility(view.toString() == "invisible" ? View.INVISIBLE : View.VISIBLE);
                    break;
            }
        }
    }

    public static void setProps(PropertiesProxy props, Map o) {
        props.eventOnChange = false;
        fromTo(o, props);
        props.eventOnChange = true;
        props.change();
    }

    public static void fromTo(Map<String, Object> propsFrom, PropertiesProxy propsTo) {
        if (propsFrom == null) return;

        for (Map.Entry<String, Object> entry : propsFrom.entrySet()) {
            propsTo.put(entry.getKey(), entry.getValue());
        }
    }
}
