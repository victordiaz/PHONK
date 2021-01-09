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

package io.phonk.gui.connectionInfo;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import io.phonk.R;
import io.phonk.events.Events;
import io.phonk.runner.base.models.Project;
import io.phonk.runner.base.utils.MLog;

public class EventManager {

    private static final String TAG = EventManager.class.getSimpleName();
    private final Context mContext;
    public ArrayList<EventLogItem> mEventList = new ArrayList<>();

    public EventManager(Context c) {
        mContext = c;
        EventBus.getDefault().register(this);
    }

    public void addEvent(String type, String detail) {
        // MLog.d(TAG, "addEvent: " + type + " " + detail);
        mEventList.add(new EventLogItem(type, detail));

        // if (Looper.myLooper() == null) Looper.prepare();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> EventBus.getDefault().post(new Events.AppUiEvent(Events.NEW_EVENT, "")));
    }

    @Subscribe
    public void onEventMainThread(Events.AppUiEvent e) {
        String action = e.getAction();
        Object value = e.getValue();

        switch (action) {
            case "stopServers":
                addEvent("server", "stopped");
                break;
            case "startServers":
                addEvent("server", "started");
                break;
            case "version":
                addEvent(action, (String) value);
                break;
            case "welcome":
                addEvent(mContext.getString(R.string.hello_emoji), (String) value);
                break;

        }
    }

    @Subscribe
    public void onEventMainThread(Events.ProjectEvent e) {
        MLog.d(TAG, "connect -> " + e.getAction());

        String action = e.getAction();
        Project p = e.getProject();

        if (action.equals(Events.PROJECT_RUN)) {
            addEvent("run ", p.getSandboxPath());
            // addEvent("qq", "qq2");
        } else if (action.equals(Events.PROJECT_STOP_ALL_AND_RUN)) {
            addEvent("stop ", p.getSandboxPath());
        } else if (action.equals(Events.PROJECT_STOP_ALL)) {
            addEvent("stop", "all projects");
        } else if (action.equals(Events.PROJECT_SAVE)) {
            addEvent("save", p.getSandboxPath());
        } else if (action.equals(Events.PROJECT_NEW)) {
            addEvent("create", p.name);
        } else if (action.equals(Events.PROJECT_RENAME)) {
            // addEvent("rename", .);
        } else if (action.equals(Events.PROJECT_FILE_MOVE)) {
            // addEvent("move", .);
        } else if (action.equals(Events.PROJECT_DELETE)) {
            addEvent("delete", p.name);
        } else if (action.equals((Events.PROJECT_DELETE_FILE))) {
            addEvent("delete", p.name);
        } else if (action.equals(Events.PROJECT_UPDATE)) {
            // mProtocoder.protoScripts.listRefresh();
        } else if (action.equals(Events.PROJECT_EDIT)) {
            addEvent("edit", p.getSandboxPath());
        } else if (action.equals(Events.PROJECT_REFRESH_LIST)) {
            // addTextToConsole("refreshing list");
        } else if (action.equals(Events.PROJECT_RUNNING)) {
            // addTextToConsole("run " + e.getProject().getSandboxPath());
            addEvent("run", e.getProject().getSandboxPath());
        }
    }

    @Subscribe
    public void onEventMainThread(Events.ExecuteCodeEvent e) {
        String code = e.getCode();
        addEvent("execute", code.substring(0, Math.min(code.length(), 10)));
    }

    @Subscribe
    public void onEventMainThread(Events.UserConnectionEvent e) {
        MLog.d(TAG, "qqq" + e);
        MLog.d(TAG, "qqq" + e.getIp());

        String str = "";
        String ip = e.getIp();
        if (e.getConnected()) {
            str = ip + " connected";
            addEvent("client connected", "from " + ip);
        } else {
            str = ip + " disconnected";
            addEvent("client diconnected", "from " + ip);
        }
    }

    @Subscribe
    public void onEventMainThread(Events.HTTPServerEvent e) {
        if (e.getWhat() == Events.PROJECT_LOAD) {
            addEvent("load", e.getProject().getSandboxPath());
        } else if (e.getWhat() == Events.EDITOR_UPLOAD) {
            addEvent("upload", e.getWhat());
        }
    }

    @Subscribe
    public void onEventMainThread(Events.FileEvent e) {
        addEvent(e.getAction(), e.getFile());
    }

    public ArrayList<EventLogItem> getEventsList() {
        return mEventList;
    }

    public class EventLogItem {
        public String type;
        public String detail;

        public EventLogItem(String type, String detail) {
            this.type = type;
            this.detail = detail;
        }
    }
}
