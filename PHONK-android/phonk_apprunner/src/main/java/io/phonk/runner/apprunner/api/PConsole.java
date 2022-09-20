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

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.phonk.runner.apidoc.annotation.PhonkMethod;
import io.phonk.runner.apidoc.annotation.PhonkMethodParam;
import io.phonk.runner.apidoc.annotation.PhonkObject;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.interpreter.AppRunnerInterpreter;
import io.phonk.runner.base.events.Events;
import io.phonk.runner.base.utils.MLog;
import io.phonk.runner.base.utils.StrUtils;

@PhonkObject
public class PConsole extends ProtoBase {

    private final boolean showTime = false;
    private final SimpleDateFormat s = new SimpleDateFormat("hh:mm:ss");

    public PConsole(AppRunner appRunner) {
        super(appRunner);
    }

    /**
     * Shows any text in the WebIDE console
     *
     * @param outputs
     * @return
     * @status TODO_EXAMPLE
     */
    @PhonkMethod
    public PConsole log(Object... outputs) {
        base_log("log", outputs);
        return this;
    }

    @PhonkMethod
    public PConsole logImage(String imgPath) {
        imgPath = imgPath + "?" + StrUtils.generateUUID();
        String img = "<img src = '" + imgPath + "'/>";

        base_log("log", img);
        return this;
    }


    /**
     * Shows any text in the WebIDE console
     *
     * @param outputs
     * @status TODO_EXAMPLE
     */
    @PhonkMethod(description = "shows any HTML text in the webIde console marked as error", example = "")
    @PhonkMethodParam(params = {"text", "text", "..."})
    public PConsole error(Object outputs) {
        base_log("log_error", outputs);

        return this;
    }

    public PConsole p_error(int type, Object outputs) {
        // MLog.d("qq", "" + type);

        switch (type) {
            case AppRunnerInterpreter.RESULT_ERROR:
                base_log("log_error", outputs);
                break;

            case AppRunnerInterpreter.RESULT_PERMISSION_ERROR:
                base_log("log_permission_error", outputs);
                break;

            case AppRunnerInterpreter.RESULT_NOT_CAPABLE:
                base_log("log_error", "This device does not support/have " + outputs);
        }

        return this;
    }


    /**
     * Clears the console
     *
     * @status TODO_EXAMPLE
     */
    @PhonkMethod(description = "clear the console", example = "")
    @PhonkMethodParam(params = {""})
    public PConsole clear() {
        send("clear", "");
        return this;
    }

    /**
     * Shows / hide the console
     *
     * @param b
     * @status TODO
     */
    @PhonkMethod(description = "show/hide the console", example = "")
    @PhonkMethodParam(params = {"boolean"})
    public PConsole show(boolean b) {
        if (b) send("show", "");
        else send("hide", "");

        return this;
    }


    /*
    @ProtoMethod(description = "Change the background color", example = "")
    @ProtoMethodParam(params = {"colorHex"})
    public PConsole backgroundColor(String colorHex) {
        String color = AndroidUtils.colorHexToHtmlRgba(colorHex);

        JSONObject values = null;
        JSONObject msg = null;
        try {
            values = new JSONObject().put("color", color);
            msg = new JSONObject().put("type", "console").put("action", "backgroundColor").put("values", values);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // send(msg);

        return this;
    }

    @ProtoMethod(description = "Log using a defined colorHex", example = "")
    @ProtoMethodParam(params = {"colorHex"})
    public PConsole logC(String text, String colorHex) {
        String color = AndroidUtils.colorHexToHtmlRgba(colorHex);

        text = getCurrentTime() + " " + text;
        JSONObject values = null;
        JSONObject msg = null;
        try {
            values = new JSONObject().put("val", text).put("color", color);
            msg = new JSONObject().put("type", "console").put("action", "logC").put("values", values);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // send(msg);

        return this;
    }

    @ProtoMethod(description = "Changes the console text size", example = "")
    @ProtoMethodParam(params = {"size"})
    public PConsole textSize(int textSize) {

        JSONObject values = null;
        JSONObject msg = null;
        try {
            values = new JSONObject().put("textSize", textSize + "px");
            msg = new JSONObject().put("type", "console").put("action", "textSize").put("values", values);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // send(msg);

        return this;
    }

    @ProtoMethod(description = "Changes the console text color", example = "")
    @ProtoMethodParam(params = {"colorHex"})
    public PConsole textColor(String colorHex) {
        String color = AndroidUtils.colorHexToHtmlRgba(colorHex);

        JSONObject values = null;
        JSONObject msg = null;
        try {
            values = new JSONObject().put("textColor", color);
            msg = new JSONObject().put("type", "console").put("action", "textColor").put("values", values);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // send(msg);

        return this;
    }

    @ProtoMethod(description = "Enable/Disable time in the log", example = "")
    @ProtoMethodParam(params = {"boolean"})
    public PConsole showTime(boolean b) {
        showTime = b;

        return this;
    }
    */

    private void base_log(String action, Object... outputs) {
        StringBuilder builder = new StringBuilder();
        if (showTime) builder.append(getCurrentTime());
        for (Object output : outputs) {
            // format the objects to json output if the object is a ReturnObject
            String out = "";

            out = output.toString();
            builder.append(" ").append(out);
        }
        String log = builder.toString();

        send(action, log);
    }

    private void send(String action, String data) {
        EventBus.getDefault().postSticky(new Events.LogEvent(action, getCurrentTime(), data));
    }

    private String getCurrentTime() {
        return s.format(new Date());
    }

    public void adbLog(String tag, String msg) {
        MLog.d(tag, msg);
    }

    public void __stop() {

    }
}

