/*
 * Part of Phonk http://www.phonk.io
 * A prototyping platform for Android devices
 *
 * Copyright (C) 2013 - 2017 Victor Diaz Barrales @victordiaz (Protocoder)
 * Copyright (C) 2017 - Victor Diaz Barrales @victordiaz (Phonk)
 *
 * Phonk is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Phonk is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Phonk. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package io.phonk.server;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import io.phonk.events.Events;

import java.lang.ref.WeakReference;

public class IDEcommunication {

    private String TAG = IDEcommunication.class.getSimpleName();
    private static IDEcommunication inst;
    public WeakReference<Context> a;
    PhonkWebsocketServer ws = null;

    public IDEcommunication(Context appActivity) {
        this.a = new WeakReference<>(appActivity);

        /*
        try {
            ws = PhonkWebsocketServer.getInstance(a.get());
            MLog.d(TAG, "websocket started");
        } catch (UnknownHostException e) {
            e.printStackTrace();
            MLog.d(TAG, "websocket :(");
        }
        */

        ws.addListener("phonkApp", new PhonkWebsocketServer.WebSocketListener() {
            @Override
            public void onUpdated(JSONObject jsonObject) {
                try {
                    String type = jsonObject.getString("type");

                    if (type.equals("project_highlight")) {
                        String folder = jsonObject.getString("folder");
                        String name = jsonObject.getString("name");

                        Events.SelectedProjectEvent evt = new Events.SelectedProjectEvent(folder, name);
                        EventBus.getDefault().post(evt);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    // Singleton (one app view, different URLs)
    public static IDEcommunication getInstance(Context a) {
        if (inst == null) {
            inst = new IDEcommunication(a);
        }
        return inst;
    }

    public void ready(boolean r) {

        JSONObject msg = new JSONObject();
        try {
            msg.put("type", "ide");
            msg.put("action", "ready");

            JSONObject values = new JSONObject();

            values.put("ready", r);
            msg.put("values", values);

        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        ws.send(msg.toString());
    }

    public void sendCustomJs(String jsString) {

        JSONObject msg = new JSONObject();
        try {
            msg.put("type", "ide");
            msg.put("action", "customjs");

            JSONObject values = new JSONObject();
            values.put("val", jsString);

            msg.put("values", values);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        ws.send(msg.toString());
    }

    public void send(JSONObject obj) {
        ws.send(obj.toString());
    }

}
