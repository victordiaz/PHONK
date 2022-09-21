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
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import io.phonk.runner.base.views.FitRecyclerView;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {
    private static final String TAG = EventsAdapter.class.getSimpleName();
    private final Context mContext;
    private final FitRecyclerView mEventsRecyclerView;
    public ArrayList<EventManager.EventLogItem> mEventList;

    public EventsAdapter(Context c, FitRecyclerView eventsRecyclerView) {
        mContext = c;
        mEventsRecyclerView = eventsRecyclerView;
    }

    public void setEventList(ArrayList<EventManager.EventLogItem> eventList) {
        this.mEventList = eventList;
        notifyDataSetChanged();
        mEventsRecyclerView.scrollToPosition(mEventList.size() - 1);
    }

    public void notifyNewEvents() {
        notifyItemInserted(mEventList.size());
        mEventsRecyclerView.scrollToPosition(mEventList.size() - 1);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final EventItemView mView;

        public ViewHolder(EventItemView v) {
            super(v);
            mView = v;
        }
    }

    public void add(EventManager.EventLogItem event) {
        mEventList.add(event);
        notifyItemInserted(mEventList.size());
        mEventsRecyclerView.scrollToPosition(mEventList.size() - 1);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        EventItemView projectItem = new EventItemView(mContext);

        return new ViewHolder(projectItem);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        EventManager.EventLogItem event = mEventList.get(position);
        holder.mView.set(event);
    }

    @Override
    public int getItemCount() {
        return mEventList.size();
    }
}
