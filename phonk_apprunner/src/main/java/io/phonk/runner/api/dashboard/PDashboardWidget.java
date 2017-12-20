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

import android.content.Context;
import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;
import io.phonk.runner.api.PDashboard;
import io.phonk.runner.api.ProtoBase;
import io.phonk.runner.api.common.ReturnInterface;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.base.utils.StrUtils;

import java.net.UnknownHostException;

public abstract class PDashboardWidget extends ProtoBase {

    private static final String TAG = PDashboardWidget.class.getSimpleName();
    protected final PDashboard.DashboardServer mDashboardServer;

    private Context mContext;

    protected ReturnInterface mCallback;

    final String D_MODULE = "dashboard";
    String mType;

    protected String mId;
    protected String name;

    public PDashboardWidget(AppRunner appRunner, PDashboard.DashboardServer dashboardServer, String type) {
        super(appRunner);

        mContext = getAppRunner().getAppContext();
        mDashboardServer = dashboardServer;
        mType = type;
        this.mId = StrUtils.generateUUID();
    }

    public void add(String name, int x, int y, int w, int h) throws JSONException, UnknownHostException {
        this.name = name;

        JSONObject values = new JSONObject()
                .put("name", name)
                .put("x", x)
                .put("y", y)
                .put("w", w)
                .put("h", h);

        sendToDashboard("add", values);
    }

    protected void sendToDashboard(String action, JSONObject values) throws JSONException {
        // wrap the values
        JSONObject msg = new JSONObject()
                .put("module", D_MODULE)
                .put("type", mType)
                .put("id", mId)
                .put("action", action)
                .put("values", values);

        // send to app that manages the websocket
        Intent i = new Intent("org.protocoder.intent.WEBEDITOR_SEND");
        i.putExtra("data", msg.toString());
        mContext.sendBroadcast(i);
    }

    @Override
    public void __stop() {
        mDashboardServer.removeListener(mId);
    }

}
