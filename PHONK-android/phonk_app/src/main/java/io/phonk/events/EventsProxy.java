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

package io.phonk.events;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import io.phonk.runner.base.utils.MLog;

/**
 * Created by victornomad on 25/02/16.
 */
public class EventsProxy {

    String TAG = EventsProxy.class.getSimpleName();

    public EventsProxy() {
        EventBus.getDefault().register(this);
    }

    public void stop() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEventMainThread(Events.ProjectEvent e) {
        MLog.d(TAG, e.getClass().getSimpleName() + " -> " + e.getAction());
    }

    @Subscribe
    public void onEventMainThread(Events.HTTPServerEvent e) {
        MLog.d(TAG, e.getClass().getSimpleName() + " -> " + e.getWhat());
    }

    // execute lines
    @Subscribe
    public void onEventMainThread(Events.ExecuteCodeEvent e) {
        MLog.d(TAG, e.getClass().getSimpleName() + " -> " + e.getCode());
    }

    //folder choose
    @Subscribe
    public void onEventMainThread(Events.FolderChosen e) {
        MLog.d(TAG, e.getClass().getSimpleName() + " -> " + e.getFullFolder());
    }

}
