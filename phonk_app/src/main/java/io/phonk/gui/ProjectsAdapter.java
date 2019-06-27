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
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import io.phonk.runner.models.Folder;
import io.phonk.runner.models.Project;

public class ProjectsAdapter extends RecyclerView.Adapter<ProjectsAdapter.ViewHolder> {

    private static final String TAG = ProjectsAdapter.class.getSimpleName();
    private ArrayList<Folder> mDataSet;
    private final boolean mListMode;
    private final Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
        }
    }

    public ProjectsAdapter(Context c, boolean listMode) {
        mContext = c;
        mListMode = listMode;
    }

    public void setArray(ArrayList<Folder> folders) {
        mDataSet = folders;
    }

    public void add(Folder folder) {
        mDataSet.add(folder);
        notifyItemInserted(mDataSet.size());
    }

    public void remove(Project p) {
        int id = findAppPosByName(p.getName());
        mDataSet.remove(id);
        notifyItemRemoved(id);
    }

    public int findAppPosByName(String appName) {
        int pos = -1;

        for (int i = 0; i < mDataSet.size(); i++) {
            String name = mDataSet.get(i).getName();

            if (name.equals(appName)) {
                pos = i;

                break;
            }
        }

        return pos;
    }

    @Override
    public int getItemViewType(int position) {
        return mDataSet.get(position).getType();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View item;

        if (viewType == Folder.PARENT) {
            item = new ProjectFolderViewItem(mContext);
        } else {
            item = new ProjectAdapterViewItem(mContext, mListMode);
        }

        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Folder f = mDataSet.get(position);

        if (getItemViewType(position) == Folder.PARENT) {
            ((ProjectFolderViewItem) holder.view).setInfo(f);
        } else {
            Project p = new Project(f.getFolder(), f.getName());
            ((ProjectAdapterViewItem) holder.view).setInfo(p);
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

}
