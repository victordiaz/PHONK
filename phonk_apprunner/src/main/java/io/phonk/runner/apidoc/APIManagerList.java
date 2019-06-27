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

    class API {

        public Class cls;
        protected Method methods;

        public API(Class cls, Object obj, String name, Method method) {
            this.cls = cls;
            this.methods = method;
        }

    }

    private static final String TAG = "MethodsExtract";

    String methodAnnotationName = "JavaScriptInterface";
    private Vector<API> apis;

    APIManagerList() {
        methodAnnotationName = "JavaScriptInterface";

        apis = new Vector<API>();
        // PackageUtils.getClasseNamesInPackage(jarName, packageName);
        // MLog.d(TAG, "" + java.lang.Class.class.getClasses().toString());
    }

    public void addObject(Object obj) {

        Class cls = obj.getClass();

        MLog.d(TAG, " -- adding new object with Class " + cls.getName() + " " + cls.getSimpleName());

        // searching fields with annotations
        Field[] attr = cls.getDeclaredFields();
        MLog.d(TAG, "Declared annotations " + cls.getDeclaredAnnotations());

        for (int i = 0; i < attr.length; i++) {

            attr[i].setAccessible(true);
            Field url = attr[i];
            String name = attr[i].getName();
            Class<?> type = attr[i].getType();

            // foreach annotation in this object
            Annotation[] a = attr[i].getAnnotations();
            for (int j = 0; j < a.length; j++) {

                String objectName = a[j].annotationType().getSimpleName();

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

        for (int i = 0; i < methods.length; i++) {

            Method method = methods[i];
            method.setAccessible(true);
            String name = methods[i].getName();

            // foreach annotation in this object
            Annotation[] a = method.getAnnotations();
            for (int j = 0; j < a.length; j++) {

                String objectName = a[j].annotationType().getSimpleName();

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

}
