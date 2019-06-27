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

package io.phonk.runner.api.widgets;

import android.content.Context;

import androidx.recyclerview.widget.GridLayoutManager;

import org.mozilla.javascript.NativeArray;

import io.phonk.runner.api.common.ReturnInterfaceWithReturn;
import io.phonk.runner.apidoc.annotation.ProtoMethod;
import io.phonk.runner.apidoc.annotation.ProtoMethodParam;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.StyleProperties;
import io.phonk.runner.base.views.FitRecyclerView;

public class PList extends FitRecyclerView {

    private final Context mContext;
    private final AppRunner mAppRunner;
    private GridLayoutManager mGridLayoutManager;
    private PViewItemAdapter mViewAdapter;

    public StyleProperties props = new StyleProperties();
    public Styler styler;

    public PList(AppRunner appRunner) {
        super(appRunner.getAppContext());
        mAppRunner = appRunner;
        mContext = appRunner.getAppContext();
    }

    public void init(NativeArray data, int numCols, ReturnInterfaceWithReturn createCallback, ReturnInterfaceWithReturn bindingCallback) {
        styler = new Styler(mAppRunner, this, props);
        styler.apply();

        mGridLayoutManager = new GridLayoutManager(mContext, numCols);
        setLayoutManager(mGridLayoutManager);
        // setLayoutManager(new StaggeredGridLayoutManager(2, VERTICAL));
        mViewAdapter = new PViewItemAdapter(mContext, data, createCallback, bindingCallback);

        // Get GridView and set adapter
        setHasFixedSize(true);

        setAdapter(mViewAdapter);
        notifyDataChanged();

        setItemAnimator(null);
    }

    public void stackFromEnd (boolean b) {
        mGridLayoutManager.setStackFromEnd(b);
    }

    public void scrollToPosition(int pos) {
        super.scrollToPosition(pos);
    }

    public PList setNumCols(int num) {
        mGridLayoutManager.setSpanCount(num);

        return this;
    }

    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {""})
    public void setItems(NativeArray data) {
        mViewAdapter.setArray(data);
    }

    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {""})
    public void clear() {

    }

    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {""})
    public void notifyDataChanged() {
        mViewAdapter.notifyDataSetChanged();
    }

}
