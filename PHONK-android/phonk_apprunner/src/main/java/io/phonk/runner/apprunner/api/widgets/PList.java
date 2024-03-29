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

import java.util.Map;

import io.phonk.runner.apidoc.annotation.PhonkMethod;
import io.phonk.runner.apidoc.annotation.PhonkMethodParam;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.api.common.ReturnInterfaceWithReturn;
import io.phonk.runner.base.views.FitRecyclerView;

public class PList extends FitRecyclerView implements PViewMethodsInterface {

    public final PropertiesProxy props = new PropertiesProxy();
    public final Styler styler;
    protected final AppRunner mAppRunner;
    private final Context mContext;
    private GridLayoutManager mGridLayoutManager;
    private PViewItemAdapter mViewAdapter;
    private int nNumCols;
    private boolean stackFromEnd;

    public PList(AppRunner appRunner, Map initProps) {
        super(appRunner.getAppContext());
        mAppRunner = appRunner;
        mContext = appRunner.getAppContext();

        styler = new Styler(appRunner, this, props);
        props.onChange((name, value) -> {
            WidgetHelper.applyViewParam(name, value, props, this, appRunner);
            styler.apply(name, value);
            apply(name, value);
        });

        props.eventOnChange = false;
        props.put("columns", 1);
        props.put("stackFromEnd", false);
        WidgetHelper.fromTo(initProps, props);
        props.eventOnChange = true;
        props.change();
    }

    public void init(
            NativeArray data,
            ReturnInterfaceWithReturn createCallback,
            ReturnInterfaceWithReturn bindingCallback
    ) {
        mGridLayoutManager = new GridLayoutManager(mContext, nNumCols);
        mGridLayoutManager.setStackFromEnd(stackFromEnd);
        setLayoutManager(mGridLayoutManager);
        mViewAdapter = new PViewItemAdapter(mContext, data, createCallback, bindingCallback);

        // Get GridView and set adapter
        setHasFixedSize(true);

        setAdapter(mViewAdapter);
        notifyDataChanged();

        setItemAnimator(null);
    }

    @PhonkMethod(description = "", example = "")
    @PhonkMethodParam(params = {""})
    public void notifyDataChanged() {
        mViewAdapter.notifyDataSetChanged();
    }

    public void stackFromEnd(boolean b) {
        props.put("stackFromEnd", b);
    }

    public PList numColumns(int num) {
        props.put("columns", num);
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

    @Override
    public void set(float x, float y, float w, float h) {
        styler.setLayoutProps(x, y, w, h);
    }

    @Override
    public void setProps(Map props) {
        WidgetHelper.setProps(this.props, props);
    }

    @Override
    public Map getProps() {
        return props;
    }

    protected void apply(String name, Object value) {
        if (name == null) {
            apply("columns");
            apply("stackFromEnd");

        } else {
            if (value == null) return;
            switch (name) {
                case "columns":
                    if (value instanceof Number) {
                        nNumCols = ((Number) value).intValue();
                        if (mGridLayoutManager != null) mGridLayoutManager.setSpanCount(nNumCols);
                    }
                    break;

                case "stackFromEnd":
                    if (value instanceof Boolean) {
                        stackFromEnd = (Boolean) value;
                        if (mGridLayoutManager != null) mGridLayoutManager.setStackFromEnd(stackFromEnd);
                    }
                    break;
            }
        }
    }

    protected void apply(String name) {
        apply(name, props.get(name));
    }
}
