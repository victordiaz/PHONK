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

package io.phonk.gui.projectbrowser.projectlist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.CycleInterpolator;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;

import io.phonk.R;
import io.phonk.events.Events;
import io.phonk.events.Events.ProjectEvent;
import io.phonk.helpers.PhonkScriptHelper;
import io.phonk.runner.base.BaseFragment;
import io.phonk.runner.base.models.Project;
import io.phonk.runner.base.utils.MLog;
import io.phonk.runner.base.views.FitRecyclerView;

@SuppressLint("NewApi")
public class ProjectListFragment extends BaseFragment {

    private String TAG = ProjectListFragment.class.getSimpleName();
    private Context mContext;

    private FitRecyclerView mGrid;
    private ConstraintLayout mEmptyGrid;

    public ArrayList<Project> mListProjects = null;
    public ProjectItemAdapter mProjectAdapter;

    public String mProjectFolder;
    boolean mListMode = true;
    public boolean mOrderByName = true;

    private ImageButton mBackToFolderButton;
    private TextView mTxtParentFolder;
    private TextView mTxtProjectFolder;
    private boolean mIsTablet = false;

    private LinearLayout mBottomBar;
    private LinearLayout mFolderPath;
    private ConstraintLayout mSelectFolder;

    private ProjectSelectedListener mListener;
    private BackClickedListener mClickBackListener;
    private int mMode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mProjectFolder = getArguments().getString("folderName", "");
        mOrderByName = getArguments().getBoolean("orderByName");
        mMode = getArguments().getInt("mode");

        mProjectAdapter = new ProjectItemAdapter(getActivity(), mListMode, mMode);
        mProjectAdapter.setListener(mListener);
    }

    public static ProjectListFragment newInstance(String folderName, int mode, boolean orderByName) {
        ProjectListFragment myFragment = new ProjectListFragment();

        Bundle args = new Bundle();
        args.putString("folderName", folderName);
        args.putInt("mode", mode);
        args.putBoolean("orderByName", orderByName);
        myFragment.setArguments(args);

        return myFragment;
    }

    public interface BackClickedListener {
        void onBackSelected();
    }

    public interface ProjectSelectedListener {
        void onProjectSelected(Project p);
        void onMultipleProjectsSelected(HashMap<Project, Boolean> projects);
    }

    public void setBackClickListener(BackClickedListener listener) {
        mClickBackListener = listener;
    }

    public void setListener(ProjectSelectedListener listener) {
        mListener = listener;
        if (mProjectAdapter != null) mProjectAdapter.setListener(listener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();

        View v;
        if (mListMode) {
            v = inflater.inflate(R.layout.projectlist_list, container, false);
        } else {
            v = inflater.inflate(R.layout.projectlist_grid, container, false);
        }

        // Get GridView and set adapter
        mGrid = v.findViewById(R.id.gridprojects);
        mGrid.setItemAnimator(new DefaultItemAnimator());

        // set the empty state
        mEmptyGrid = v.findViewById(R.id.empty_grid_view);
        // checkEmptyState();
        registerForContextMenu(mGrid);

        // if (mProjectFolder != "") loadFolder(mProjectFolder);

        mBackToFolderButton = v.findViewById(R.id.backToFolders);
        mTxtParentFolder = v.findViewById(R.id.parentFolder);
        mTxtProjectFolder = v.findViewById(R.id.folder);
        mFolderPath = v.findViewById(R.id.folderPath);
        mSelectFolder = v.findViewById(R.id.select_folder);

        mBackToFolderButton.setOnClickListener(view -> {
            mClickBackListener.onBackSelected();
        });

        mIsTablet = getResources().getBoolean(R.bool.isTablet);
        LinearLayout llFolderLocation = v.findViewById(R.id.folderLocation2);

        mBottomBar = v.findViewById(R.id.bottombar);
        mBottomBar.setVisibility(View.GONE);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    private void checkEmptyState() {
        //check if a has been loaded
        if (mListProjects == null) {
            showProjectList(false);
            return;
        }

        //if empty we show, hey! there is no projects!
        if (mListProjects.isEmpty()) showProjectList(false);
        else showProjectList(true);
    }

    private void showProjectList(boolean b) {
        if (b) {
            mGrid.setVisibility(View.VISIBLE);
            mEmptyGrid.setVisibility(View.GONE);
        } else {
            mGrid.setVisibility(View.GONE);
            mEmptyGrid.setVisibility(View.VISIBLE);
        }
    }

    public void goTo(int pos) {
        if (pos != -1) mGrid.smoothScrollToPosition(pos);
    }

    public void clear() {
        if (mListProjects != null) mListProjects.clear();
        mProjectAdapter.notifyDataSetChanged();
    }

    public void notifyAddedProject() {
        checkEmptyState();
    }

    public void loadFolder(String folder, String project) {
        clear();
        mProjectFolder = folder + '/' + project;

        mSelectFolder.setVisibility(View.GONE);
        mFolderPath.setVisibility(View.VISIBLE);
        mTxtParentFolder.setText(folder);
        mTxtProjectFolder.setText(project);

        mListProjects = PhonkScriptHelper.listProjects(mProjectFolder, mOrderByName);
        mProjectAdapter.setArray(mListProjects);
        mGrid.setAdapter(mProjectAdapter);
        // mGrid.clearAnimation();
        // mGrid.startAnimation(mAnim);

        notifyAddedProject();

        final Context context = mGrid.getContext();
        final LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(context, R.anim.fav_grid_anim);

        mGrid.setLayoutAnimation(controller);
        mGrid.getAdapter().notifyDataSetChanged();
        mGrid.scheduleLayoutAnimation();

        MLog.d(TAG, "loading " + mProjectFolder);
    }

    public View getItemView(String projectName) {
        return mGrid.findViewWithTag(projectName);
    }

    /*
     * UI fancyness
     */
    public void projectRefresh(String projectName) {
        getItemView(projectName).animate().alpha(0).setDuration(500).setInterpolator(new CycleInterpolator(1));
    }

    //TODO reenable this
    /*
    public void resetHighlighting() {
        for (int i = 0; i < mListProjects.size(); i++) {
            //mListProjects.get(i).selected = false;
        }

        for (int i = 0; i < mGrid.getChildCount(); i++) {
            ProjectItem v = (ProjectItem) mGrid.getChildAt(i);
            if (v.isHighlighted()) {
                v.setHighlighted(false);
            }
        }
    }
    */

    public View highlight(String projectName, boolean b) {
        View v = mGrid.findViewWithTag(projectName);
        v.setSelected(b);
        //TODO reenable this
        //mProjectAdapter.mData.get(mProjectAdapter.findAppIdByName(projectName)).selected = true;
        v.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);

        return v;
    }

    /*
     * Events
     */

    //run project
    @Subscribe
    public void onEventMainThread(ProjectEvent evt) {
        String action = evt.getAction();

        switch (action) {
            case Events.PROJECT_RUN:
                Project p = evt.getProject();
                projectRefresh(p.getName());
                MLog.d(TAG, "> Event (Run project feedback)" + p.getName());
                break;
            case Events.PROJECT_NEW:
                MLog.d(TAG, "notify data set changed");
                mProjectAdapter.add(evt.getProject());
                break;
            case Events.PROJECT_DELETE:
                mProjectAdapter.remove(evt.getProject());
                break;
            case Events.PROJECT_REFRESH_LIST:
                // loadFolder(mProjectFolder);
                break;
        }

    }
}
