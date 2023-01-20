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

import io.phonk.runner.apidoc.annotation.PhonkMethod;
import io.phonk.runner.apidoc.annotation.PhonkMethodParam;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.api.ProtoBase;
import io.phonk.runner.base.utils.StrUtils;

public class _PDashboardImage extends ProtoBase {

    private static final String TAG = "_PDashboardImage";
    String id;

    public _PDashboardImage(AppRunner appRunner) {
        super(appRunner);
    }


    @PhonkMethod(description = "", example = "")
    public void add(String url, int x, int y, int w, int h) throws UnknownHostException, JSONException {
        this.id = StrUtils.generateUUID();

        JSONObject values = new JSONObject().put("id", id)
                .put("url", url)
                .put("type", "image")
                .put("x", x)
                .put("y", y)
                .put("w", w)
                .put("h", h);

        JSONObject msg = new JSONObject().put("type", "widget").put("action", "add").put("values", values);

        //TODO change to events
        //CustomWebsocketServer.getInstance(getContext()).send(msg);
    }


    @PhonkMethod(description = "change image with a provided url", example = "")
    @PhonkMethodParam(params = {"url"})
    public void changeImage(String url) throws JSONException, UnknownHostException {

        JSONObject values = new JSONObject().put("id", id).put("type", "label").put("url", url);

        JSONObject msg = new JSONObject().put("type", "widget").put("action", "changeImage").put("values", values);

        //TODO change to events
        //CustomWebsocketServer.getInstance(getContext()).send(msg);
    }

    @Override
    public void __stop() {

    }
}
