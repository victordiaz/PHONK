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

package io.phonk.runner.apprunner.api.other;

import org.mozilla.javascript.NativeObject;

import java.util.ArrayList;

import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.api.ProtoBase;
import io.phonk.runner.base.utils.StrUtils;

public class PEvents extends ProtoBase {
    ArrayList<EventItem> eventsList;

    public interface EventCB {
        void event(NativeObject obj);

    }

    public PEvents(AppRunner appRunner) {
        super(appRunner);
        eventsList = new ArrayList<EventItem>();
    }

    public String add(String name, EventCB callback) {
        String id = StrUtils.generateUUID();
        eventsList.add(new EventItem(id, name, callback));

        return id;
    }

    public void remove(String id) {
        for (int i = 0; i < eventsList.size(); i++) {
            if (id.equals(eventsList.get(i).id)) {
                eventsList.remove(i);
                break;
            }
        }
    }

    public void sendEvent(String name, NativeObject obj) {
        //get all matching listeners and send event

        for (int i = 0; i < eventsList.size(); i++) {
            if (name.equals(eventsList.get(i).name)) {
                eventsList.get(i).cb.event(obj);
            }
        }
    }

    class EventItem {
        public final String id;
        public final String name;
        public final PEvents.EventCB cb;

        EventItem(String id, String name, PEvents.EventCB cb) {
            this.id = id;
            this.name = name;
            this.cb = cb;
        }
    }

    @Override
    public void __stop() {
        for (int i = 0; i < eventsList.size(); i++) {
            eventsList.remove(i);
        }
    }

}
