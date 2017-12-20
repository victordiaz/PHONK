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

package io.phonk.runner.api.dashboard;

import android.graphics.Color;

import org.json.JSONException;
import org.json.JSONObject;
import io.phonk.runner.api.ProtoBase;
import io.phonk.runner.apprunner.AppRunner;

import java.net.UnknownHostException;

public class _PDashboardBackground extends ProtoBase {

    private static final String TAG = "_PDashboardBackground";

    public _PDashboardBackground(AppRunner appRunner) {
        super(appRunner);
    }

    // --------- JDashboard add ---------//
    public interface jDashboardAddCB {
        void event();
    }


    public void updateColor(String hex) throws JSONException, UnknownHostException {
        int c = Color.parseColor(hex);
        float alpha = (float) (Color.alpha(c) / 255.0); //html uses normalized values
        int r = Color.red(c);
        int g = Color.green(c);
        int b = Color.blue(c);

        JSONObject values = new JSONObject()
                .put("type", "background")
                .put("a", alpha)
                .put("r", r)
                .put("g", g)
                .put("b", b);

        JSONObject msg = new JSONObject()
                .put("type", "widget")
                .put("action", "add")
                .put("values", values);

        //TODO change to events
        //CustomWebsocketServer.getInstance(getContext()).send(msg);
    }

    @Override
    public void __stop() {

    }
}
