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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.phonk.R;

public class EventItemView extends LinearLayout {

    private static final String TAG = EventItemView.class.getSimpleName();

    private View mItemView;
    private final Context mContext;
    private TextView txtEventType;
    private TextView txtEventDetail;

    public EventItemView(Context context) {
        super(context);
        this.mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.mItemView = inflater.inflate(R.layout.event_list_item, this, true);
        txtEventType = mItemView.findViewById(R.id.txtProjectName);
        txtEventDetail = mItemView.findViewById(R.id.txtEventDetail);
    }

    public void set(String type, String detail) {
        txtEventType.setText(type);
        txtEventDetail.setText(detail);
    }
}
