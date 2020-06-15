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
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import org.mozilla.javascript.NativeArray;

import io.phonk.runner.apprunner.api.common.ReturnInterfaceWithReturn;
import io.phonk.runner.apprunner.api.common.ReturnObject;
import io.phonk.runner.base.utils.MLog;


public class PViewItemAdapter extends RecyclerView.Adapter<PViewItemAdapter.ViewHolder> {
    private static final String TAG = PViewItemAdapter.class.getSimpleName();
    private final Context mContext;
    private final ReturnInterfaceWithReturn mCreating;
    private final ReturnInterfaceWithReturn mBinding;

    public NativeArray mData;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final View mView;

        public ViewHolder(View v) {
            super(v);
            mView = v;
        }
    }

    public PViewItemAdapter(Context c, NativeArray data, ReturnInterfaceWithReturn creating, ReturnInterfaceWithReturn binding) {
        mContext = c;
        mData = data;

        // MLog.d(TAG, "" + data);

        mCreating = creating;
        mBinding = binding;
    }

    public void setArray(NativeArray data) {
        this.mData = data;
    }

    public void add(Object o) {
        mData.add(o);
        notifyItemInserted(mData.size());
    }

    public void remove(int pos) {
        mData.remove(pos);
        notifyItemRemoved(pos);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MLog.d(TAG, "create 1");

        View v = (View) mCreating.event(null);
        // MLog.d(TAG, "create 2" + v);

        PViewItemAdapter.ViewHolder vh = new PViewItemAdapter.ViewHolder(v);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // MLog.d(TAG, "bind " + position);

        // Object o = mData.get(position);

        ReturnObject ro = new ReturnObject();
        ro.put("view", holder.mView);
        ro.put("position", position);
        mBinding.event(ro);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

}
