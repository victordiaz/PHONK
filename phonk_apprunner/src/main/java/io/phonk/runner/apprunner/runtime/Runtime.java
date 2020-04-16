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

package io.phonk.runner.apprunner.runtime;

import android.util.Log;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;
import com.eclipsesource.v8.V8ScriptCompilationException;
import com.eclipsesource.v8.V8ScriptExecutionException;
import com.eclipsesource.v8.V8Value;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import io.phonk.runner.apidoc.annotation.PhonkField;
import io.phonk.runner.apidoc.annotation.PhonkMethod;
import io.phonk.runner.base.utils.MLog;

public class Runtime {
    private static final String TAG = Runtime.class.getSimpleName();

    private V8 v8runtime;

    public Runtime() {
        v8runtime = V8.createV8Runtime();
    }

    public void executeScript(String script) {
        try {
            MLog.d("qq", "executeVoidScript");
            v8runtime.executeVoidScript(script);
        } catch (V8ScriptCompilationException e) {
            Log.e(TAG, e.toString());
            // TODO Show error in app
        } catch (V8ScriptExecutionException e) {
            Log.e(TAG, e.toString());
            // TODO Show error in app
        }
    }

    public Boolean executeBooleanScript(String script) {
        Boolean ret = null;

        try {
            ret = v8runtime.executeBooleanScript(script);
        } catch (V8ScriptCompilationException e) {
            Log.e(TAG, e.toString());
        }

        return ret;
    }

    public void addJavaObject(String name, Object javaObject) {
        this.addJavaObject(name, javaObject, null);
    }

    public void addJavaObject(String name, Object javaObject, V8Object v8Object) {
        Log.d(TAG, "adding Java Object " + name);

        V8Object v8o = new V8Object(v8runtime);

        if (v8Object == null) {
            v8runtime.add(name, v8o);
        } else {
            v8Object.add(name, v8o);
        }

        Field[] fields = javaObject.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(PhonkField.class)) {
                Log.d(TAG, "field: " + field.getName());
                try {
                    Log.d("qq2", "" + field.get(javaObject));
                    addJavaObject(field.getName(), field.get(javaObject), v8o);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        Method[] methods = javaObject.getClass().getDeclaredMethods();
        for (Method method : methods) {
            // Log.d(TAG, method.getName() + " method");
            if (method.isAnnotationPresent(PhonkMethod.class)) {
                Log.d(TAG, method.getName() + " (" + method.getReturnType() + ")");

                Class<?>[] params = method.getParameterTypes();
                for (Class<?> param : params) {
                    Log.d(TAG, "\t└ " + param.getSimpleName());
                }

                v8o.registerJavaMethod(javaObject, method.getName(), method.getName(), params);
            }
        }
    }


    public V8Object createV8o(Object javaObject) {
        MLog.d("qq", "2.- ");

        V8Object v8o = new V8Object(v8runtime);

        Field[] fields = javaObject.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(PhonkField.class)) {
                Log.d(TAG, "field: " + field.getName());
                try {
                    Log.d("qq2", "" + field.get(javaObject));
                    addJavaObject(field.getName(), field.get(javaObject), v8o);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        Method[] methods = javaObject.getClass().getDeclaredMethods();
        for (Method method : methods) {
            // Log.d(TAG, method.getName() + " method");
            if (method.isAnnotationPresent(PhonkMethod.class)) {
                Log.d(TAG, method.getName() + " (" + method.getReturnType() + ")");

                Class<?>[] params = method.getParameterTypes();
                for (Class<?> param : params) {
                    Log.d(TAG, "\t└ " + param.getSimpleName());
                }

                v8o.registerJavaMethod(javaObject, method.getName(), method.getName(), params);
            }
        }

        return v8o;
    }

    public V8Object addV8ObjectFromJavaObject(Object object) {
        MLog.d("qq", "1.-- ");
        MLog.d("qq", "" + object.getClass().getName());

        V8Object v8o = createV8o(object);
        MLog.d("qq", "1.-- isReleased" + v8o.isReleased() + " " + v8o);
        v8o.add("_ref", v8o);

        String objectName = object.getClass().getSimpleName().toLowerCase().substring(1);
        addV8Object(objectName, v8o);
        MLog.d("qq", "2.-- isReleased" + v8o.isReleased());

        return v8o;
    }

    public void addV8Object(String objectName, V8Value obj) {
        MLog.d("qq", "3.- " + objectName + " " + obj);

        v8runtime.add(objectName, obj);
    }

    public V8Object getObject(String objectName) {
        return v8runtime.getObject(objectName);
    }

    public V8Object createObject() {
        return new V8Object(v8runtime);
    }

    public V8Array createArray() {
        return new V8Array(v8runtime);
    }

    public int getInteger(String varName) {
        return v8runtime.getInteger(varName);
    }

    public V8 getV8Runtime() {
        return v8runtime;
    }

    public void stop() {
        v8runtime.close();
    }

    public void callJsFunction(String fn) {
        this.executeScript(fn);
    }
}
