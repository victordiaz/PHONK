package com.example.myapplication;

import android.util.Log;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;
import com.eclipsesource.v8.V8ScriptCompilationException;
import com.eclipsesource.v8.V8Value;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

class Runtime {
    private static final String TAG = Runtime.class.getSimpleName();

    private V8 v8runtime;

    Runtime() {
        v8runtime = V8.createV8Runtime();
    }

    @PhonkMethod
    void executeScript(String script) {
        try {
            v8runtime.executeScript(script);
        } catch (V8ScriptCompilationException e) {
            Log.e(TAG, e.toString());
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

    @PhonkMethod
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

    public void addV8Object(String objectName, V8Value obj) {
        v8runtime.add(objectName, obj);
    }

    public V8Object getObject(String objectName) {
        return (V8Object) v8runtime.getObject(objectName);
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
}
