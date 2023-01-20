/*
 * Part of Phonk http://www.phonk.io
 * A prototyping platform for Android devices
 *
 * Copyright (C) 2013 - 2017 Victor Diaz Barrales @victordiaz (Protocoder)
 * Copyright (C) 2017 - Victor Diaz Barrales @victordiaz (Phonk)
 *
 * Phonk is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Phonk is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Phonk. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package io.phonk.runner.apprunner.api.widgets;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.Map;

import io.phonk.runner.apidoc.annotation.PhonkClass;
import io.phonk.runner.apprunner.AppRunner;

@PhonkClass
public class PViewPager extends ViewPager implements PViewMethodsInterface {
    // this is a props proxy for the user
    public final StylePropertiesProxy props = new StylePropertiesProxy();
    // the props are transformed / accessed using the styler object
    public final Styler styler;
    final MyAdapter mAdapter;

    public PViewPager(AppRunner appRunner, Map initProps) {
        super(appRunner.getAppContext());

        styler = new Styler(appRunner, this, props);
        props.eventOnChange = false;
        Styler.fromTo(initProps, props);
        props.eventOnChange = true;
        styler.apply();
        setPageMargin(0);
        setPadding(0, 0, 0, 0);

        mAdapter = new MyAdapter();
        this.setAdapter(mAdapter);
    }

    public PViewPager add(View v) {
        this.mAdapter.addView(v);
        this.mAdapter.notifyDataSetChanged();
        return this;
    }

    public PViewPager page(int p) {
        this.setCurrentItem(p);
        return this;
    }

    @Override
    public void set(float x, float y, float w, float h) {
        styler.setLayoutProps(x, y, w, h);
    }

    static class MyAdapter extends PagerAdapter {
        final ArrayList<View> list = new ArrayList<>();

        MyAdapter() {
        }

        public void addView(View v) {
            list.add(v);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View v = list.get(position);
            container.addView(v, 0);
            return v;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }
    }

    @Override
    public void setProps(Map style) {
        styler.setProps(style);
    }

    @Override
    public Map getProps() {
        return props;
    }
}
