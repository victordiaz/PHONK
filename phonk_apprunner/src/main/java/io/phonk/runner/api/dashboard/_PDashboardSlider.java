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
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.base.utils.StrUtils;

import java.net.UnknownHostException;

public class _PDashboardSlider extends ProtoBase {

    private static final String TAG = "_PDashboardSlider";
    String id;
    private jDashboardSliderAddCB mCallback;

    public _PDashboardSlider(AppRunner appRunner) {
        super(appRunner);
    }

    // --------- JDashboardSlider add ---------//
    public interface jDashboardSliderAddCB {
        void event(double val);
    }

    public void add(String name, int x, int y, int w, int h, int min, int max)
            throws UnknownHostException, JSONException {
        this.id = StrUtils.generateUUID();

        JSONObject values = new JSONObject()
                .put("id", id)
                .put("name", name)
                .put("type", "slider")
                .put("x", x)
                .put("y", y)
                .put("w", w)
                .put("h", h)
                .put("min", min)
                .put("max", max);

        JSONObject msg = new JSONObject()
                .put("type", "widget")
                .put("action", "add")
                .put("values", values);

        //TODO change to events

        /*
        CustomWebsocketServer.getInstance(getContext()).addListener(id, new WebSocketListener() {
            @Override
            public void onUpdated(JSONObject jsonObject) {
                try {
                    final double val = jsonObject.getDouble("val");
                    mHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            if (mCallback != null) mCallback.event(val);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        CustomWebsocketServer.getInstance(getContext()).send(msg);
        */
    }

    public void onChange(final jDashboardSliderAddCB callbackfn) throws UnknownHostException {
        mCallback = callbackfn;
    }

    //TODO this method doesnt work yet
    //
    //@APIMethod(description = "change the slider value", example = "")
    //@APIParam(params = { "value" })
    public void position(float position) throws UnknownHostException, JSONException {
        JSONObject msg = new JSONObject();

        msg.put("type", "widget");
        msg.put("action", "setValue");

        JSONObject values = new JSONObject();
        values.put("id", id);
        values.put("type", "label");
        values.put("val", position);
        msg.put("values", values);

        //TODO change to events
        //CustomWebsocketServer ws = CustomWebsocketServer.getInstance(getContext());
        //ws.send(msg);
    }

    @Override
    public void __stop() {

    }
}
