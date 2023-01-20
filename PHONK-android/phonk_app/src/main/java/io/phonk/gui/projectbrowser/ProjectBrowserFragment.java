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

package io.phonk.gui.projectbrowser;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import org.greenrobot.eventbus.EventBus;

import io.phonk.R;
import io.phonk.events.Events;
import io.phonk.gui.projectbrowser.folderlist.FolderListFragment;
import io.phonk.gui.projectbrowser.projectlist.ProjectListFragment;
import io.phonk.gui.settings.PhonkSettings;

public class ProjectBrowserFragment extends Fragment {

    public static int MODE_NORMAL = 0;
    public static int MODE_SINGLE_PICK = 1;
    public static int MODE_MULTIPLE_PICK = 2;
    public ViewPager mViewPager;
    private FolderListFragment mFolderListFragment;
    private ProjectListFragment mProjectListFragment;
    private ProjectBrowserPagerAdapter mProjectBrowserPagerAdapter;
    private ProjectListFragment.ProjectSelectedListener mListener;
    private int mMode;

    public static ProjectBrowserFragment newInstance(int mode) {
        ProjectBrowserFragment f = new ProjectBrowserFragment();
        Bundle args = new Bundle();
        args.putInt("mode", mode);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) mMode = getArguments().getInt("mode", -1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.project_browser_fragment, container, false);

        mFolderListFragment = FolderListFragment.newInstance(PhonkSettings.EXAMPLES_FOLDER, true);
        mProjectListFragment = ProjectListFragment.newInstance("", mMode, true);
        mProjectBrowserPagerAdapter = new ProjectBrowserPagerAdapter(
                getChildFragmentManager(),
                mFolderListFragment,
                mProjectListFragment
        );

        mFolderListFragment.setListener((folder, name) -> {
            mProjectListFragment.loadFolder(folder, name);
            mViewPager.setCurrentItem(1);
            Events.FolderChosen ev = new Events.FolderChosen(folder, name);
            EventBus.getDefault().post(ev);
        });

        mProjectListFragment.setBackClickListener(() -> mViewPager.setCurrentItem(0));
        mProjectListFragment.setListener(mListener);

        // Set up the ViewPager with the sections adapter.
        mViewPager = rootView.findViewById(R.id.pager);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(mProjectBrowserPagerAdapter);
        mViewPager.setCurrentItem(0);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public void setProjectClickListener(ProjectListFragment.ProjectSelectedListener listener) {
        mListener = listener;
    }

}
