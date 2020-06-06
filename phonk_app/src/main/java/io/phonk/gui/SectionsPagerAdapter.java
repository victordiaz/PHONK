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

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;

import io.phonk.gui.folderchooser.FolderListFragment;
import io.phonk.gui.projectlist.ProjectListFragment;
import io.phonk.runner.base.utils.MLog;

public class SectionsPagerAdapter extends PagerAdapter {
    private final FragmentManager mFragmentManager;
    private final SparseArray mFragments;
    private FragmentTransaction mCurTransaction;

    public SectionsPagerAdapter(FragmentManager fm, EmptyFragment mEmptyFragment, CombinedFolderAndProjectFragment mCombinedFolderAndProjectFragment) {
        mFragmentManager = fm;
        mFragments = new SparseArray<>();
        mFragments.put(0, mEmptyFragment);

        mFragments.put(1, mCombinedFolderAndProjectFragment);
    }

    public SectionsPagerAdapter(FragmentManager fm, EmptyFragment mEmptyFragment, FolderListFragment mFolderListFragment, ProjectListFragment mProjectListFragment) {
        mFragmentManager = fm;
        mFragments = new SparseArray<>();
        mFragments.put(0, mEmptyFragment);
        mFragments.put(1, mFolderListFragment);
        mFragments.put(2, mProjectListFragment);
    }

    public Fragment getItem(int position) {
        return (Fragment) mFragments.get(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = getItem(position);
        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }

        mCurTransaction.add(container.getId(), fragment, "fragment:" + position);

        return mFragments.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }
        mCurTransaction.detach((Fragment) mFragments.get(position));
        // mFragments.remove(position);
    }

    @Override
    public boolean isViewFromObject(View view, Object fragment) {
        return ((Fragment) fragment).getView() == view;
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        if (mCurTransaction != null) {
            mCurTransaction.commitAllowingStateLoss();
            mCurTransaction = null;
            mFragmentManager.executePendingTransactions();
        }
    }

    @Override
    public int getCount() {
        // MLog.d("fff", "getCount " + mFragments.size());
        return mFragments.size();
    }
}