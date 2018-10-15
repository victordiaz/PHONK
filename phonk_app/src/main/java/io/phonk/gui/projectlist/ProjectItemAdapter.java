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

package io.phonk.gui.projectlist;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;

import io.phonk.runner.models.Project;

public class ProjectItemAdapter extends RecyclerView.Adapter<ProjectItemAdapter.ViewHolder> {
    private static final String TAG = ProjectItemAdapter.class.getSimpleName();
    private final Context mContext;

    public ArrayList<Project> mProjectList = new ArrayList<>();
    private final boolean mListMode;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ProjectItem mView;

        public ViewHolder(ProjectItem v) {
            super(v);
            mView = v;
        }
    }

    /*
     * ProjectItemAdapter
     */
    public ProjectItemAdapter(Context c, boolean listMode) {
        mContext = c;
        mListMode = listMode;
    }

    public void setArray( ArrayList<Project> projectList) {
        this.mProjectList = projectList;
    }

    public void add(Project project) {
        mProjectList.add(project);
        notifyItemInserted(mProjectList.size());
    }

    public void remove(Project p) {
        int id = findAppPosByName(p.getName());
        mProjectList.remove(id);
        notifyItemRemoved(id);
    }

    public int findAppIdByName(String appName) {
        int id = -1;

        for (int i = 0; i < mProjectList.size(); i++) {
            String name = mProjectList.get(i).getName();
            if (name.equals(appName)) {
                id = i;
                break;
            }
        }

        return id;
    }

    public int findAppPosByName(String appName) {
        int pos = -1;

        for (int i = 0; i < mProjectList.size(); i++) {
            String name = mProjectList.get(i).getName();
            // MLog.d(TAG, "name " + name);

            if (name.equals(appName)) {
                pos = i; //(int) mProjectAdapter.getItemId(i);

                break;
            }
        }

        return pos;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ProjectItem projectItem = new ProjectItem(mContext,/* mPlf, */ mListMode);
        ViewHolder vh = new ProjectItemAdapter.ViewHolder(projectItem);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Project p = mProjectList.get(position);
        holder.mView.setProjectInfo(p);
    }

    @Override
    public int getItemCount() {
        return mProjectList.size();
    }

}
