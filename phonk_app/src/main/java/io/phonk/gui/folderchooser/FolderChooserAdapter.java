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

package io.phonk.gui.folderchooser;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import io.phonk.R;
import io.phonk.events.Events;
import io.phonk.runner.base.utils.MLog;

import java.util.ArrayList;

public class FolderChooserAdapter extends RecyclerView.Adapter<FolderChooserAdapter.ViewHolder> {

    private static final String TAG = FolderChooserAdapter.class.getSimpleName();
    private final ArrayList<FolderAdapterData> mDataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public ViewHolder(int viewType, LinearLayout v) {
            super(v);

            switch (viewType) {
                case FolderAdapterData.TYPE_TITLE:
                    textView = (TextView) v.findViewById(R.id.textType);

                    break;
                case (FolderAdapterData.TYPE_FOLDER_NAME):
                    textView = (TextView) v.findViewById(R.id.textFolder);

                    break;
            }
        }
    }

    public FolderChooserAdapter(ArrayList<FolderAdapterData> folders) {
        mDataSet = folders;
    }

    @Override
    public int getItemViewType(int position) {
        return mDataSet.get(position).item_type;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout t = null;
        if (viewType == FolderAdapterData.TYPE_TITLE) {
            t = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.folderchooser_title_view, parent, false);
        }  else if (viewType == FolderAdapterData.TYPE_FOLDER_NAME) {
            t = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.folderchooser_folder_view, parent, false);
        }
        return new ViewHolder(viewType, t);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        int type = mDataSet.get(position).item_type;
        final String name = mDataSet.get(position).name;
        final String folder = mDataSet.get(position).parentFolder;

        switch (type) {
            case FolderAdapterData.TYPE_TITLE:
                holder.textView.setText(name);
                holder.textView.setOnClickListener(null);
                break;
            case FolderAdapterData.TYPE_FOLDER_NAME:
                holder.textView.setText(name);
                holder.textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MLog.d(TAG, "> Event (folderChosen) " + folder + "/" + name);

                        Events.FolderChosen ev = new Events.FolderChosen(folder, name);
                        EventBus.getDefault().post(ev);
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

}
