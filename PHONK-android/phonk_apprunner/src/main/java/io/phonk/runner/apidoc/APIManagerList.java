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

package io.phonk.runner.apidoc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Vector;

import io.phonk.runner.base.utils.MLog;

public class APIManagerList {

    private static final String TAG = "MethodsExtract";
    private final Vector<API> apis;
    String methodAnnotationName = "JavaScriptInterface";
    APIManagerList() {
        methodAnnotationName = "JavaScriptInterface";
        apis = new Vector<>();
    }

    public void addObject(Object obj) {
        Class cls = obj.getClass();

        MLog.d(TAG, " -- adding new object with Class " + cls.getName() + " " + cls.getSimpleName());

        // searching fields with annotations
        Field[] attr = cls.getDeclaredFields();
        MLog.d(TAG, "Declared annotations " + cls.getDeclaredAnnotations());

        for (Field field : attr) {

            field.setAccessible(true);
            Field url = field;
            String name = field.getName();
            Class<?> type = field.getType();

            // foreach annotation in this object
            Annotation[] a = field.getAnnotations();
            for (Annotation annotation : a) {

                String objectName = annotation.annotationType().getSimpleName();

                // if (objectName.equals(annotationName)) {
                //
                // // guardar aqui la referencia al objeto
                // API api = new API(cls, obj, name, attr[i]);
                // apis.add(api);
                //
                // }

            }

        }

        // ------------------ get declared methods
        Method[] methods = cls.getDeclaredMethods();

        for (Method method : methods) {

            method.setAccessible(true);
            String name = method.getName();

            // foreach annotation in this object
            Annotation[] a = method.getAnnotations();
            for (Annotation annotation : a) {

                String objectName = annotation.annotationType().getSimpleName();

                if (objectName.equals(methodAnnotationName)) {

                    MLog.d(TAG, "annotation method: " + method + " " + name);

                    // save object reference
                    API qq = new API(cls, obj, name, method);
                    apis.add(qq);

                }

            }

        }

    }

    public Object getValue(Object obj, Field attr) {
        Object value = null;

        // get value
        try {
            value = attr.get(obj);
        } catch (IllegalArgumentException e1) {
            e1.printStackTrace();
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        }

        return value;
    }

    public void callMethod(Object obj, Method method) {
        try {
            method.invoke(obj, null);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    class API {

        public final Class cls;
        protected final Method methods;

        public API(Class cls, Object obj, String name, Method method) {
            this.cls = cls;
            this.methods = method;
        }

    }

}
