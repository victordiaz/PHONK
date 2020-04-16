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

import io.phonk.runner.apprunner.api.PDashboard;
import io.phonk.runner.apidoc.annotation.PhonkMethod;
import io.phonk.runner.apidoc.annotation.PhonkMethodParam;
import io.phonk.runner.apprunner.AppRunner;

public class PDashboardWebview extends PDashboardWidget {

    public PDashboardWebview(AppRunner appRunner, PDashboard.DashboardServer dashboardServer) {
        super(appRunner, dashboardServer, "webview");
    }

    @PhonkMethod(description = "change the text", example = "")
    @PhonkMethodParam(params = {"text"})
    public void setUrl(String text) throws UnknownHostException, JSONException {

        JSONObject values = new JSONObject()
                .put("url", text);

        sendToDashboard("setUrl", values);
    }

    @PhonkMethod(description = "change the text", example = "")
    @PhonkMethodParam(params = {"text"})
    public void setHTML(String text) throws UnknownHostException, JSONException {

        JSONObject values = new JSONObject()
                .put("html", text);

        sendToDashboard("setHTML", values);
    }

}
