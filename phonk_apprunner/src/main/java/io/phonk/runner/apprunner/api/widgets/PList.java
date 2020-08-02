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

import android.content.Context;

import androidx.recyclerview.widget.GridLayoutManager;

import org.mozilla.javascript.NativeArray;

import io.phonk.runner.apidoc.annotation.PhonkMethod;
import io.phonk.runner.apidoc.annotation.PhonkMethodParam;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.api.common.ReturnInterfaceWithReturn;
import io.phonk.runner.base.views.FitRecyclerView;

public class PList extends FitRecyclerView {

    private final Context mContext;
    protected final AppRunner mAppRunner;
    private GridLayoutManager mGridLayoutManager;
    private PViewItemAdapter mViewAdapter;

    public StylePropertiesProxy props = new StylePropertiesProxy();
    public Styler styler;
    private int nNumCols = 1;

    public PList(AppRunner appRunner) {
        super(appRunner.getAppContext());
        mAppRunner = appRunner;
        mContext = appRunner.getAppContext();
    }

    public void init(NativeArray data, ReturnInterfaceWithReturn createCallback, ReturnInterfaceWithReturn bindingCallback) {
        styler = new Styler(mAppRunner, this, props);
        styler.apply();

        mGridLayoutManager = new GridLayoutManager(mContext, nNumCols);
        setLayoutManager(mGridLayoutManager);
        // setLayoutManager(new StaggeredGridLayoutManager(2, VERTICAL));
        mViewAdapter = new PViewItemAdapter(mContext, data, createCallback, bindingCallback);

        // Get GridView and set adapter
        setHasFixedSize(true);

        setAdapter(mViewAdapter);
        notifyDataChanged();

        setItemAnimator(null);
    }

    public void stackFromEnd(boolean b) {
        mGridLayoutManager.setStackFromEnd(b);
    }

    public void scrollToPosition(int pos) {
        super.scrollToPosition(pos);
    }

    public PList numColumns(int num) {
        nNumCols = num;
        if (mGridLayoutManager != null) mGridLayoutManager.setSpanCount(num);

        return this;
    }

    @PhonkMethod(description = "", example = "")
    @PhonkMethodParam(params = {""})
    public void items(NativeArray data) {
        mViewAdapter.setArray(data);
    }

    @PhonkMethod(description = "", example = "")
    @PhonkMethodParam(params = {""})
    public void clear() {
    }

    @PhonkMethod(description = "", example = "")
    @PhonkMethodParam(params = {""})
    public void notifyDataChanged() {
        mViewAdapter.notifyDataSetChanged();
    }

}
