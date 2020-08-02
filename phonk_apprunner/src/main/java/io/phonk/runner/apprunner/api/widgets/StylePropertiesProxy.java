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

package io.phonk.runner.apprunner.api.widgets;

import org.mozilla.javascript.Scriptable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.phonk.runner.base.utils.GSONUtil;
import io.phonk.runner.base.utils.MLog;

public class StylePropertiesProxy implements Scriptable, Map<String, Object> {

    private static final java.lang.String TAG = StylePropertiesProxy.class.getSimpleName();
    private HashMap<String, Object> values = new HashMap<>();
    private OnChangeListener changeListener;
    public boolean eventOnChange = true;

    public StylePropertiesProxy() {

    }

    @Override
    public String getClassName() {
        return "Object";
    }

    @Override
    public Object get(String name, Scriptable start) {
        // MLog.d(TAG, "get 1: " + name + " " + values.get(name));

        return values.get(name);
    }

    @Override
    public void put(String name, Scriptable start, Object value) {
        // MLog.d(TAG, "put 1: " + name + " : " + value + " " + changeListener);
        values.put(name, value);

        if (changeListener != null && eventOnChange) changeListener.event(name, value);
    }

    @Override
    public Object get(int index, Scriptable start) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean has(String name, Scriptable start) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean has(int index, Scriptable start) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void put(int index, Scriptable start, Object value) {
        // TODO Auto-generated method stub
        // MLog.d(TAG, "put 3: " + index + " : " + value + " " + changeListener);
    }

    @Override
    public void delete(String name) {
        // TODO Auto-generated method stub

    }

    @Override
    public void delete(int index) {
        // TODO Auto-generated method stub
    }

    @Override
    public Scriptable getPrototype() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setPrototype(Scriptable prototype) {
        // TODO Auto-generated method stub

    }

    @Override
    public Scriptable getParentScope() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setParentScope(Scriptable parent) {
        // TODO Auto-generated method stub

    }

    @Override
    public Object[] getIds() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getDefaultValue(Class<?> hint) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean hasInstance(Scriptable instance) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int size() {
        return values.size();
    }

    @Override
    public boolean isEmpty() {
        return values.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return values.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return values.containsKey(value);
    }

    @Override
    public Object get(Object key) {
        // MLog.d(TAG, "get 2: " + key + " " + values.get(key));
        return values.get(key);
    }

    @Override
    public Object put(String key, Object value) {
        MLog.d(TAG, "put 3: " + key + " " + values.get(key));
        values.put(key, value);

        return value;
    }

    @Override
    public Object remove(Object key) {
        return values.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> m) {
        MLog.d(TAG, "putAll: ");

        values.putAll(m);
    }

    @Override
    public void clear() {
        values.clear();
    }

    @Override
    public Set<String> keySet() {
        return values.keySet();
    }

    @Override
    public Collection<Object> values() {
        return values.values();
    }

    @Override
    public Set<java.util.Map.Entry<String, Object>> entrySet() {
        return values.entrySet();
    }

    public void onChange(OnChangeListener listener) {
        changeListener = listener;
    }

    public interface OnChangeListener {
        void event(String name, Object value);
    }

    public String apply(HashMap<String, Object> styleProps) {
        for (Map.Entry<String, Object> entry : styleProps.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }

        return "qq";
    }

    public String toString() {
        return GSONUtil.getInstance().getGson().toJson(this);
    }
}