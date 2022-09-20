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

package io.phonk.runner.apprunner.api;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.HashMap;

import io.phonk.runner.apidoc.annotation.PhonkMethod;
import io.phonk.runner.apidoc.annotation.PhonkMethodParam;
import io.phonk.runner.apidoc.annotation.PhonkObject;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.api.dashboard.PDashboardButton;
import io.phonk.runner.apprunner.api.dashboard.PDashboardMainObject;
import io.phonk.runner.apprunner.api.dashboard.PDashboardText;
import io.phonk.runner.apprunner.api.dashboard.PDashboardWebview;
import io.phonk.runner.base.utils.MLog;

@PhonkObject
public class PDashboard extends ProtoBase {

    private final DashboardServer mDashboardServer;
    private final PDashboardMainObject mDashboard;
    final String TAG = PDashboard.class.getSimpleName();

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

        final HashMap<String, ServerListenerCallback> mCallbacks = new HashMap<>();

        DashboardServer() {
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

            mHandler.post(() -> {
                if (callback != null) callback.onUpdated(o);
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


    @PhonkMethod(description = "add a button in the dashboard", example = "")
    @PhonkMethodParam(params = {"name", "x", "y", "w", "h", "function()"})
    public PDashboardButton addButton(String name, int x, int y, int w, int h) throws UnknownHostException, JSONException {
        PDashboardButton pWebAppButton = new PDashboardButton(getAppRunner(), mDashboardServer);
        pWebAppButton.add(name, x, y, w, h);

        return pWebAppButton;
    }


    @PhonkMethod(description = "add a text in the dashboard", example = "")
    @PhonkMethodParam(params = {"name", "x", "y", "size", "hexColor"})
    public PDashboardText addText(String name, int x, int y, int width, int height) throws UnknownHostException, JSONException {

        PDashboardText pWebAppText = new PDashboardText(getAppRunner(), mDashboardServer);
        pWebAppText.add(name, x, y, width, height);

        return pWebAppText;
    }


    @PhonkMethod(description = "add a HTML content in the dashboard", example = "")
    @PhonkMethodParam(params = {"htmlFile", "x", "y"})
    public PDashboardWebview addHtml(int x, int y, int width, int height) throws UnknownHostException, JSONException {

        PDashboardWebview pWebAppHTML = new PDashboardWebview(getAppRunner(), mDashboardServer);
        pWebAppHTML.add("", x, y, width, height);

        return pWebAppHTML;
    }


    @PhonkMethod(description = "show/hide the dashboard", example = "")
    @PhonkMethodParam(params = {"boolean"})
    public void show(boolean b) {
        JSONObject msg = new JSONObject();
        try {
            JSONObject values = new JSONObject().put("val", b);
            msg.put("type", "widget").put("action", "showDashboard").put("values", values);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }


    final BroadcastReceiver dashboardBroadcastReceiver = new BroadcastReceiver() {
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
