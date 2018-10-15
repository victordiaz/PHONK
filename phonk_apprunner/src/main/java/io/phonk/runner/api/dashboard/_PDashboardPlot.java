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

import org.json.JSONException;
import org.json.JSONObject;
import io.phonk.runner.api.ProtoBase;
import io.phonk.runner.apidoc.annotation.ProtoMethod;
import io.phonk.runner.apidoc.annotation.ProtoMethodParam;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.base.utils.StrUtils;

import java.net.UnknownHostException;

public class _PDashboardPlot extends ProtoBase {

    private static final String TAG = "_PDashboardPlot";
    String id;

    public _PDashboardPlot(AppRunner appRunner) {
        super(appRunner);
    }

    public void add(String name, int x, int y, int w, int h, float minLimit, float maxLimit)
            throws UnknownHostException, JSONException {
        this.id = StrUtils.generateUUID();

        JSONObject values = new JSONObject()
                .put("id", id)
                .put("name", name)
                .put("type", "plot")
                .put("x", x)
                .put("y", y)
                .put("w", w)
                .put("h", h)
                .put("minLimit", minLimit)
                .put("maxLimit", maxLimit);

        JSONObject msg = new JSONObject()
                .put("type", "widget")
                .put("action", "add")
                .put("values", values);

        //TODO change to events
        //CustomWebsocketServer.getInstance(getContext()).send(msg);
    }


    @ProtoMethod(description = "update the plot with a given value", example = "")
    @ProtoMethodParam(params = {"value"})
    public void update(float val) throws UnknownHostException, JSONException {

        JSONObject values = new JSONObject()
                .put("id", id)
                .put("type", "plot")
                .put("val", val);

        JSONObject msg = new JSONObject()
                .put("type", "widget")
                .put("action", "update")
                .put("values", values);

        //TODO change to events
        //CustomWebsocketServer.getInstance(getContext()).send(msg);
    }

    @Override
    public void __stop() {

    }
}
