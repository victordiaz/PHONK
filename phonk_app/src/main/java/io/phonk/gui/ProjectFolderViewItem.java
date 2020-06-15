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

package io.phonk.gui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.phonk.R;
import io.phonk.runner.base.models.Folder;

public class ProjectFolderViewItem extends LinearLayout {

    private static final String TAG = ProjectFolderViewItem.class.getSimpleName();

    private View mItemView;
    private final Context c;

    private TextView textViewName;

    public ProjectFolderViewItem(Context context) {
        super(context);
        this.c = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mItemView = inflater.inflate(R.layout.projectlist_item_folder_list, this, true);

        textViewName = mItemView.findViewById(R.id.customViewText);
    }

    public void setText(String text) {
        textViewName.setText(text);
    }

    public void setInfo(Folder f) {
        setText(f.getName());
    }

}
