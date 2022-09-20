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
import java.util.Calendar;
import java.util.Date;

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

    public void addEvent(String type, String detail, int icon) {
        // MLog.d(TAG, "addEvent: " + type + " " + detail);
        mEventList.add(new EventLogItem(type, detail, icon));

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
                addEvent("server", "server stopped", R.drawable.ic_public_24dp);
                break;
            case "startServers":
                addEvent("server", "server started", R.drawable.ic_public_24dp);
                break;
            case "version":
                addEvent(action, (String) value, -1);
                break;
            case "welcome":
                addEvent("welcome", (String) value, -1); // R.drawable.ic_baseline_chat_bubble_24);
                break;

        }
    }

    @Subscribe
    public void onEventMainThread(Events.ProjectEvent e) {
        MLog.d(TAG, "connect -> " + e.getAction());

        String action = e.getAction();
        Project p = e.getProject();

        if (action.equals(Events.PROJECT_RUN)) {
            addEvent(action, p.getSandboxPath(), R.drawable.ic_baseline_play_arrow_24);
        } else if (action.equals(Events.PROJECT_STOP_ALL_AND_RUN)) {
            addEvent(action, p.getSandboxPath(), R.drawable.ic_action_stop);
        } else if (action.equals(Events.PROJECT_STOP_ALL)) {
           // addEvent("stop", "all projects", R.drawable.ic_action_stop, true);
        } else if (action.equals(Events.PROJECT_SAVE)) {
            addEvent(action, p.getSandboxPath(), R.drawable.ic_save_24dp);
        } else if (action.equals(Events.PROJECT_NEW)) {
            addEvent(action, p.name, -1);
        } else if (action.equals(Events.PROJECT_RENAME)) {
            // addEvent("rename", .);
        } else if (action.equals(Events.PROJECT_FILE_MOVE)) {
            // addEvent("move", .);
        } else if (action.equals(Events.PROJECT_DELETE)) {
            addEvent(action, p.name, -1);
        } else if (action.equals((Events.PROJECT_DELETE_FILE))) {
            addEvent(action, p.name, -1);
        } else if (action.equals(Events.PROJECT_UPDATE)) {
            // mProtocoder.protoScripts.listRefresh();
        } else if (action.equals(Events.PROJECT_EDIT)) {
            addEvent(action, p.getSandboxPath(), -1);
        } else if (action.equals(Events.PROJECT_REFRESH_LIST)) {
            // addTextToConsole("refreshing list");
        } else if (action.equals(Events.PROJECT_RUNNING)) {
            // addTextToConsole("run " + e.getProject().getSandboxPath());
            addEvent(action, e.getProject().getSandboxPath(), R.drawable.ic_baseline_play_arrow_24);
        }
    }

    @Subscribe
    public void onEventMainThread(Events.ExecuteCodeEvent e) {
        String code = e.getCode();
        addEvent("execute", code.substring(0, Math.min(code.length(), 10)), -1);
    }

    @Subscribe
    public void onEventMainThread(Events.UserConnectionEvent e) {
        String ip = e.getIp();
        if (e.getConnected()) {
            addEvent("connected", ip + " connected", R.drawable.ic_computer_24dp);
        } else {
            addEvent("disconnected", ip + " disconnected", R.drawable.ic_computer_24dp);
        }
    }

    @Subscribe
    public void onEventMainThread(Events.HTTPServerEvent e) {
        if (e.getWhat() == Events.PROJECT_LOAD) {
            addEvent("load", e.getProject().getSandboxPath(), -1);
        } else if (e.getWhat() == Events.EDITOR_UPLOAD) {
            addEvent("upload", e.getWhat(), -1);
        }
    }

    @Subscribe
    public void onEventMainThread(Events.FileEvent e) {
        addEvent(e.getAction(), e.getFile(), -1);
    }

    public ArrayList<EventLogItem> getEventsList() {
        return mEventList;
    }

    public class EventLogItem {
        public final Calendar date;
        public String type;
        public String detail;
        public int icon;
        public boolean showType;

        public EventLogItem(String type, String detail, int icon) {
            this.date = Calendar.getInstance();
            this.type = type;
            this.icon = icon;
            this.detail = detail;
        }
    }
}
