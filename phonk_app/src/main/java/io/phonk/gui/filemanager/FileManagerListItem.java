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

package io.phonk.gui.filemanager;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.phonk.R;
import io.phonk.server.model.ProtoFile;

import java.lang.ref.WeakReference;

public class FileManagerListItem extends LinearLayout {

    private ImageView mImageView;
    private TextView mTextView;

    private WeakReference<View> v;
    private Context c;

    public FileManagerListItem(Context context) {
        super(context);
        this.c = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.v = new WeakReference<View>(inflater.inflate(R.layout.filemanager_file_view, this, true));

        mImageView = (ImageView) v.get().findViewById(R.id.img_file);
        mTextView = (TextView) v.get().findViewById(R.id.txt_file_name);
    }

    public FileManagerListItem(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void setProtoFile(ProtoFile protoFile) {
        mImageView.setImageResource(getIcon(protoFile.type));
        mTextView.setText(protoFile.name);
    }

    private int getIcon(String type) {
        return type.equals("folder") ? R.drawable.ic_folder_black_24dp : R.drawable.ic_insert_drive_file_black_24dp;
    }
}

