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

package io.phonk.runner.api;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import org.json.JSONException;
import org.json.JSONObject;
import io.phonk.runner.api.dashboard.PDashboardButton;
import io.phonk.runner.api.dashboard.PDashboardMainObject;
import io.phonk.runner.api.dashboard.PDashboardText;
import io.phonk.runner.api.dashboard.PDashboardWebview;
import io.phonk.runner.apidoc.annotation.ProtoMethod;
import io.phonk.runner.apidoc.annotation.ProtoMethodParam;
import io.phonk.runner.apidoc.annotation.ProtoObject;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.base.utils.MLog;

import java.net.UnknownHostException;
import java.util.HashMap;

@ProtoObject
public class PDashboard extends ProtoBase {

    private final DashboardServer mDashboardServer;
    private final PDashboardMainObject mDashboard;
    String TAG = PDashboard.class.getSimpleName();

    public PDashboard(AppRunner appRunner) {
        super(appRunner);

        mDashboardServer = new DashboardServer();

        // register log broadcast
        IntentFilter filterSendDashboard = new IntentFilter();
        filterSendDashboard.addAction("io.phonk.intent.WEBEDITOR_RECEIVER");
        appRunner.getAppContext().registerReceiver(dashboardBroadcastReceiver, filterSendDashboard);

        mDashboard = new PDashboardMainObject(getAppRunner(), mDashboardServer);
    }

    public interface ServerListenerCallback {
        void onUpdated(JSONObject jsonObject);
    }

    public class DashboardServer {

        HashMap<String, ServerListenerCallback> mCallbacks = new HashMap<>();

        DashboardServer () {
            // start websockets server
        }

        public void onEvent(JSONObject r) throws JSONException {
            String status = (String) r.get("status");

        }

        public void addListener(String id, ServerListenerCallback callback) {
            mCallbacks.put(id, callback);
        }

        public void run(final JSONObject o) throws JSONException {
            String id = (String) o.get("id");

            final ServerListenerCallback callback = mCallbacks.get(id);

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (callback != null) callback.onUpdated(o);
                }
            });
        }

        public void removeListener(String id) {
            mCallbacks.remove(id);
        }

        public void stop() {
        }
    }

    public PDashboardMainObject get() {
        return mDashboard;
    }


    @ProtoMethod(description = "add a button in the dashboard", example = "")
    @ProtoMethodParam(params = {"name", "x", "y", "w", "h", "function()"})
    public PDashboardButton addButton(String name, int x, int y, int w, int h) throws UnknownHostException, JSONException {
        PDashboardButton pWebAppButton = new PDashboardButton(getAppRunner(), mDashboardServer);
        pWebAppButton.add(name, x, y, w, h);

        return pWebAppButton;
    }



    @ProtoMethod(description = "add a text in the dashboard", example = "")
    @ProtoMethodParam(params = {"name", "x", "y", "size", "hexColor"})
    public PDashboardText addText(String name, int x, int y, int width, int height) throws UnknownHostException, JSONException {

        PDashboardText pWebAppText = new PDashboardText(getAppRunner(), mDashboardServer);
        pWebAppText.add(name, x, y, width, height);

        return pWebAppText;
    }


    @ProtoMethod(description = "add a HTML content in the dashboard", example = "")
    @ProtoMethodParam(params = {"htmlFile", "x", "y"})
    public PDashboardWebview addHtml(int x, int y, int width, int height) throws UnknownHostException, JSONException {

        PDashboardWebview pWebAppHTML = new PDashboardWebview(getAppRunner(), mDashboardServer);
        pWebAppHTML.add("", x, y, width, height);

        return pWebAppHTML;
    }




    @ProtoMethod(description = "show/hide the dashboard", example = "")
    @ProtoMethodParam(params = {"boolean"})
    public void show(boolean b) {
        JSONObject msg = new JSONObject();
        try {
            JSONObject values = new JSONObject().put("val", b);
            msg.put("type", "widget").put("action", "showDashboard").put("values", values);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }



    /*


    @ProtoMethod(description = "add a plot in the dashboad", example = "")
    @ProtoMethodParam(params = {"name", "x", "y", "w", "h", "minLimit", "maxLimit"})
    public _PDashboardPlot addPlot(String name, int x, int y, int w, int h, float minLimit, float maxLimit)
            throws UnknownHostException, JSONException {

        _PDashboardPlot pWebAppPlot = new _PDashboardPlot(getAppRunner());
        pWebAppPlot.add(name, x, y, w, h, minLimit, maxLimit);

        return pWebAppPlot;
    }



    @ProtoMethod(description = "add a slider in the dashboard", example = "")
    @ProtoMethodParam(params = {"name", "x", "y", "w", "h", "min", "max", "function(num)"})
    public _PDashboardSlider addSlider(String name, int x, int y, int w, int h, int min, int max) throws UnknownHostException, JSONException {

        _PDashboardSlider pWebAppSlider = new _PDashboardSlider(getAppRunner());
        pWebAppSlider.add(name, x, y, w, h, min, max);

        return pWebAppSlider;
    }

    @ProtoMethod(description = "add a input box in the dashboard", example = "")
    @ProtoMethodParam(params = {"name", "x", "y", "w", "h", "function(text)"})
    public _PDashboardInput addInput(String name, int x, int y, int w, int h)
            throws UnknownHostException, JSONException {

        _PDashboardInput pWebAppInput = new _PDashboardInput(getAppRunner());
        pWebAppInput.add(name, x, y, w, h);

        return pWebAppInput;
    }

    @ProtoMethod(description = "add an image in the dashboard", example = "")
    @ProtoMethodParam(params = {"url", "x", "y", "w", "h"})
    public _PDashboardImage addImage(String url, int x, int y, int w, int h) throws UnknownHostException, JSONException {

        _PDashboardImage pWebAppImage = new _PDashboardImage(getAppRunner());
        pWebAppImage.add(url, x, y, w, h);

        return pWebAppImage;
    }

    @ProtoMethod(description = "add a camera preview in the dashboard", example = "")
    @ProtoMethodParam(params = {"url", "x", "y", "w", "h"})
    public _PDashboardVideoCamera addCameraPreview(int x, int y, int w, int h) throws UnknownHostException, JSONException {

        _PDashboardVideoCamera pWebAppVideoCamera = new _PDashboardVideoCamera(getAppRunner());
        pWebAppVideoCamera.add(x, y, w, h);

        return pWebAppVideoCamera;
    }

    @ProtoMethod(description = "add a custom widget in the dashboard", example = "")
    @ProtoMethodParam(params = {"url", "x", "y", "w", "h", "function(obj)"})
    public _PDashboardCustomWidget addCustomWidget(String url, int x, int y, int w, int h, _PDashboardCustomWidget.jDashboardAddCB callback) throws UnknownHostException, JSONException {

        _PDashboardCustomWidget pWebAppCustom = new _PDashboardCustomWidget(getAppRunner());
        pWebAppCustom.add(url, x, y, w, h, callback);

        return pWebAppCustom;
    }

    @ProtoMethod(description = "change the background color (HEX format) of the dashboard", example = "")
    @ProtoMethodParam(params = {"hexColor"})
    public _PDashboardBackground backgroundColor(String hex) throws JSONException, UnknownHostException {
        _PDashboardBackground pDashboardBackground = new _PDashboardBackground(getAppRunner());
        pDashboardBackground.updateColor(hex);

        return pDashboardBackground;
    }

    //TODO use events
    private void initKeyEvents(final jDashboardKeyPressed callbackfn) throws UnknownHostException, JSONException {

        String id = StrUtils.generateUUID();

        JSONObject values = new JSONObject()
                .put("id", id)
                .put("type", "keyevent")
                .put("enabled", true);

        JSONObject msg = new JSONObject()
                .put("type", "widget")
                .put("action", "add")
                .put("values", values);


        CustomWebsocketServer.getInstance(getContext()).send(msg);
        CustomWebsocketServer.getInstance(getContext()).addListener(id, new CustomWebsocketServer.WebSocketListener() {

            @Override
            public void onUpdated(final JSONObject jsonObject) {
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            callbackfn.event(jsonObject.getInt("val"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });


    }

    // --------- JDashboard add ---------//
    public interface jDashboardKeyPressed {
        void event(int val);
    }

    @ProtoMethod(description = "show/hide the dashboard", example = "")
    @ProtoMethodParam(params = {"boolean"})
    public void onKeyPressed(jDashboardKeyPressed callback) throws UnknownHostException, JSONException {
        initKeyEvents(callback);
    }
    */



    BroadcastReceiver dashboardBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MLog.d(TAG, intent.getAction());

            MLog.d(TAG, "received in PDashboardMainObject");
            String msg = intent.getStringExtra("data");

            MLog.d(TAG, "msg -----> " + msg);

            try {
                JSONObject msgJSON = new JSONObject(msg);
                // String id = (String) msgJSON.get("id");
                mDashboardServer.run(msgJSON);

            } catch (JSONException e) {
                e.printStackTrace();
            }


            //
        }
    };


    @Override
    public void __stop() {
        getAppRunner().getAppContext().unregisterReceiver(dashboardBroadcastReceiver);
    }
}
