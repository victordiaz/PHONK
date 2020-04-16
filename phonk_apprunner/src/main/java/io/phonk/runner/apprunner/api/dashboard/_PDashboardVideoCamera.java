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

package io.phonk.runner.apprunner.api.dashboard;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.UnknownHostException;

import io.phonk.runner.apprunner.api.ProtoBase;
import io.phonk.runner.apidoc.annotation.PhonkMethod;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.base.utils.StrUtils;

public class _PDashboardVideoCamera extends ProtoBase {

    private static final String TAG = "_PDashboardVideoCamera";
    String id;


    //TODO this is just mContext scaffold needs to be implemented
    public _PDashboardVideoCamera(AppRunner appRunner) {
        super(appRunner);
    }


    @PhonkMethod(description = "", example = "")
    public void add(int x, int y, int w, int h) throws UnknownHostException, JSONException {
        this.id = StrUtils.generateUUID();

        JSONObject values = new JSONObject()
                .put("id", id)
                .put("type", "camera")
                .put("x", x)
                .put("y", y)
                .put("w", w)
                .put("h", h);

        JSONObject msg = new JSONObject()
                .put("type", "widget")
                .put("action", "add")
                .put("values", values);

        //TODO change to events
        //CustomWebsocketServer.getInstance(getContext()).send(msg);

    }


    @PhonkMethod(description = "", example = "")
    public void update(String encodedImage) throws JSONException, UnknownHostException {
        JSONObject values = new JSONObject()
                .put("id", id)
                .put("type", "widget")
                .put("src", encodedImage);

        JSONObject msg = new JSONObject()
                .put("type", "widget")
                .put("action", "updateCamera")
                .put("values", values);

        //TODO change to events
        //CustomWebsocketServer.getInstance(getContext()).send(msg);
    }

    @Override
    public void __stop() {

    }
}
