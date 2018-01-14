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

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.CycleInterpolator;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import io.phonk.R;
import io.phonk.events.Events;
import io.phonk.events.Events.ProjectEvent;
import io.phonk.helpers.PhonkScriptHelper;
import io.phonk.runner.base.BaseFragment;
import io.phonk.runner.base.utils.MLog;
import io.phonk.runner.base.views.FitRecyclerView;
import io.phonk.runner.models.Project;

@SuppressLint("NewApi")
public class ProjectListFragment extends BaseFragment {

    private String TAG = ProjectListFragment.class.getSimpleName();
    private Context mContext;

    private FitRecyclerView mGrid;
    private GridLayoutManager mLayoutManager;
    private LinearLayout mEmptyGrid;

    public ArrayList<Project> mListProjects = null;
    public ProjectItemAdapter mProjectAdapter;

    public String mProjectFolder;
    boolean mListMode = true;
    public boolean mOrderByName = true;
    public int num = 0;
    public static int totalNum = 0;

    private ImageButton mBackToFolderButton;
    private TextView mTxtParentFolder;
    private TextView mTxtFolder;
    private boolean mIsTablet = false;

    private LinearLayout mBottomBar;
    private LinearLayout mFolderPath;
    private LinearLayout mSelectFolder;

    public ProjectListFragment() {
        num = totalNum++;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mProjectFolder = getArguments().getString("folderName", "");
        //mProjectFolder = "projects";
        MLog.d(TAG, "showing " + mProjectFolder);
        mOrderByName = getArguments().getBoolean("orderByName");
        // mListMode = (boolean) UserPreferences.getInstance().get("apps_in_list_mode");

        mProjectAdapter = new ProjectItemAdapter(getActivity(), mListMode);
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
        mGrid = (FitRecyclerView) v.findViewById(R.id.gridprojects);
        mGrid.setItemAnimator(new DefaultItemAnimator());

        // set the empty state
        mEmptyGrid = (LinearLayout) v.findViewById(R.id.empty_grid_view);
        // checkEmptyState();
        registerForContextMenu(mGrid);

        if (mProjectFolder != "") loadFolder(mProjectFolder);

        mBackToFolderButton = (ImageButton) v.findViewById(R.id.backToFolders);
        mTxtParentFolder = (TextView) v.findViewById(R.id.parentFolder);
        mTxtFolder = (TextView) v.findViewById(R.id.folder);
        mFolderPath = (LinearLayout) v.findViewById(R.id.folderPath);
        mSelectFolder = (LinearLayout) v.findViewById(R.id.select_folder);

        mBackToFolderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new Events.AppUiEvent("page", 1));
                // show folderlist
                // if (isShown)
                // else
            }
        });

        mIsTablet = getResources().getBoolean(R.bool.isTablet);
        LinearLayout llFolderLocation = (LinearLayout) v.findViewById(R.id.folderLocation2);
        if (mIsTablet) llFolderLocation.setVisibility(View.GONE);

        // TODO add the bottom bar any day.....
        mBottomBar = (LinearLayout) v.findViewById(R.id.bottombar);
        mBottomBar.setVisibility(View.GONE);
        /*
        mBottomBar.setTranslationY(AndroidUtils.dpToPixels(mContext, 52));
        mBottomBar.animate().translationY(AndroidUtils.dpToPixels(mContext, 0)).setDuration(5000).start();
        */

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

        //TODO reenable
        //if (!AndroidUtils.isWear(getActivity())) {
        //    ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        //}
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    public static ProjectListFragment newInstance(String folderName, boolean orderByName) {
        ProjectListFragment myFragment = new ProjectListFragment();

        Bundle args = new Bundle();
        args.putString("folderName", folderName);
        args.putBoolean("orderByName", orderByName);
        myFragment.setArguments(args);

        return myFragment;
    }

    private void checkEmptyState() {
        //check if a has been loaded
        if (mListProjects == null) {
            showProjectList(false);

            return;
        }

        //if empty we show, hey! there is no projects!
        if (mListProjects.isEmpty()) {
            showProjectList(false);
        } else {
            showProjectList(true);
        }
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

    // public View getViewByName(String appName) {
    //   int pos = findAppPosByName(appName);
    //View view = mProjectAdapter.getView(pos, null, null);

    // return null;
    //}

    public void goTo(int pos) {
        if (pos != -1) mGrid.smoothScrollToPosition(pos);
    }

    public void clear() {
        if (mListProjects != null) mListProjects.clear();
       // mGrid.removeAllViews();
        mProjectAdapter.notifyDataSetChanged();
    }

    public void notifyAddedProject() {
        checkEmptyState();
    }

    public void loadFolder(String folder) {
        clear();

        mProjectFolder = folder;

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
            case Events.PROJECTLIST_SHOW_BOTTOM_BAR:
                break;
            case Events.PROJECTLIST_HIDE_BOTTOM_BAR:
                break;
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
        }

    }

    // folder choose
    @Subscribe
    public void onEventMainThread(Events.FolderChosen e) {
        MLog.d(TAG, "< Event (folderChosen)");
        String folder = e.getFullFolder();
        loadFolder(folder);

        mSelectFolder.setVisibility(View.GONE);
        mFolderPath.setVisibility(View.VISIBLE);
        mTxtParentFolder.setText(e.getParent());
        mTxtFolder.setText(e.getName());
    }

}
