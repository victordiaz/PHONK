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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import io.phonk.R;

public class EventItemView extends LinearLayout {

    private static final String TAG = EventItemView.class.getSimpleName();

    final SimpleDateFormat mFormat = new SimpleDateFormat("hh:mm:ss");

    private final View mItemView;
    private final Context mContext;
    private final TextView txtEventType;
    private final TextView txtEventDetail;
    private final TextView txtEventDate;
    private final ImageView imgIcon;

    public EventItemView(Context context) {
        super(context);
        this.mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.mItemView = inflater.inflate(R.layout.connection_info_item, this, true);
        txtEventType = mItemView.findViewById(R.id.txtType);
        txtEventDetail = mItemView.findViewById(R.id.txtEventDetail);
        txtEventDate = mItemView.findViewById(R.id.txtEventDate);
        imgIcon = mItemView.findViewById(R.id.imgEventDetail);
    }

    public void set(EventManager.EventLogItem event) {
        String date = mFormat.format(event.date.getTime());

        txtEventType.setText(event.type);
        txtEventDetail.setText(event.detail);
        txtEventDate.setText("[" + date + "]");

        if (event.icon != -1) {
            imgIcon.setImageResource(event.icon);
        } else {
            imgIcon.setVisibility(View.GONE);
        }

        if (!event.showType) {
            txtEventType.setVisibility(View.GONE);
        }
    }
}
