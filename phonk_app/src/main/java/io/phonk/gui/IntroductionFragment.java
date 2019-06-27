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

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.phonk.R;
import io.phonk.runner.base.BaseFragment;

@SuppressLint("NewApi")
public class IntroductionFragment extends BaseFragment {

    private String TAG = IntroductionFragment.class.getSimpleName();
    private Context mContext;
    private ViewPager mPager;
    private IntroductionPagerAdapter mPagerAdapter;


    public IntroductionFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();

        View v = inflater.inflate(R.layout.introduction_fragment, container, false);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = v.findViewById(R.id.pager);
        mPagerAdapter = new IntroductionPagerAdapter();
        mPager.setAdapter(mPagerAdapter);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public static IntroductionFragment newInstance() {
        IntroductionFragment myFragment = new IntroductionFragment();

        return myFragment;
    }

    private class IntroductionPagerAdapter extends PagerAdapter {
        public IntroductionPagerAdapter() {
            super();
        }

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
//            View v = getActivity().getLayoutInflater().inflate(...);
//            ((ViewPager) collection).addView(v,0);

//            return v;

            int resId = 0;
            switch (position) {
                case 0:
                    resId = R.id.page_one;
                    break;
                case 1:
                    resId = R.id.page_two;
                    break;
            }
            return getActivity().findViewById(resId);
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((TextView) view);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

}
