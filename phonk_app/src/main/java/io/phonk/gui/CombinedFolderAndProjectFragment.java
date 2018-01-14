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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.phonk.R;
import io.phonk.gui.folderchooser.FolderListFragment;
import io.phonk.gui.projectlist.ProjectListFragment;

public class CombinedFolderAndProjectFragment extends Fragment {

    private static FolderListFragment mFolderListFragment;
    private static ProjectListFragment mProjectListFragment;
    private View v;

    public CombinedFolderAndProjectFragment() {
    }

    public static CombinedFolderAndProjectFragment newInstance(FolderListFragment folderListFragment, ProjectListFragment projectListFragment) {
        CombinedFolderAndProjectFragment fragment = new CombinedFolderAndProjectFragment();
        mFolderListFragment = folderListFragment;
        mProjectListFragment = projectListFragment;

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        v = inflater.inflate(R.layout.fragment_combined, container, false);

        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.folders, mFolderListFragment);
        fragmentTransaction.add(R.id.projects, mProjectListFragment);
        fragmentTransaction.commit();

        return v;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

}
