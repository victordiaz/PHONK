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

import com.google.gson.Gson;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import io.phonk.runner.apidoc.annotation.PhonkMethod;
import io.phonk.runner.apidoc.annotation.PhonkMethodParam;
import io.phonk.runner.base.utils.MLog;

/*
 * Usage
 * 		APIManager.getInstance().addClass(JAndroid.class);
 *		APIManager.getInstance().addClass(JUI.class);
 *		APIManager.getInstance().getDocumentation();
 *
 */

public class APIManager {

    private static final String TAG = "APIManager";
    private static APIManager instance;
    private static int methodCount = 0;
    HashMap<String, API> apis = new HashMap<>();
    APIManagerDoc doc = new APIManagerDoc();
    public APIManager() {

    }

    public static APIManager getInstance() {
        if (instance == null) {
            instance = new APIManager();
        }
        return instance;
    }

    /**
     * add a new class to extract the methods
     *
     * @param c
     */
    public void addClass(Class c, boolean b) {

        try {
            // api docs
            APIManagerClass apiClass = new APIManagerClass();
            apiClass.name = c.getSimpleName().substring(1).toLowerCase();
            apiClass.isMainObject = b;
            MLog.d(TAG, "" + c.getName());

            // getting all the methods
            Method[] m = c.getDeclaredMethods();
            for (Method element : m) {

                // get method
                APIManagerMethod apiMethod = new APIManagerMethod();
                apiMethod.id = methodCount++;
                apiMethod.parent = apiClass.name;
                apiMethod.name = element.getName();

                // get parameter types
                Class<?>[] param = element.getParameterTypes();
                String[] paramsType = new String[param.length];

                for (int j = 0; j < param.length; j++) {
                    String p = param[j].getSimpleName();
                    paramsType[j] = p;
                }
                apiMethod.paramsType = paramsType;

                // return type
                apiMethod.returnType = element.getReturnType().getSimpleName();

                // get method information
                if (!apiMethod.name.contains("$")) {
                    Annotation[] annotations = element.getDeclaredAnnotations();
                    // check if annotation exists and add apidocs
                    for (Annotation annotation2 : annotations) {

                        // description and example
                        if (annotation2.annotationType().getSimpleName().equals(PhonkMethod.class.getSimpleName())) {
                            apiMethod.description = ((PhonkMethod) annotation2).description();
                            apiMethod.example = ((PhonkMethod) annotation2).example();

                        }

                        // get parameters names
                        if (annotation2.annotationType()
                                .getSimpleName()
                                .equals(PhonkMethodParam.class.getSimpleName())) {
                            apiMethod.parametersName = ((PhonkMethodParam) annotation2).params();
                            MLog.d(TAG, "getting names " + apiMethod.parametersName);
                        }

                    }

                    apiClass.apiMethods.add(apiMethod);
                }
            }

            doc.apiClasses.add(apiClass);

            // classes and methods
            // apis.put(c.getSimpleName(), new API(c, m));

        } catch (Throwable e) {
            System.err.println(e);
        }

    }

    public HashMap<String, API> getAPIs() {

        return apis;
    }

    public void listAPIs() {
        Iterator it = apis.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();

            API p = (API) pairs.getValue();
            Method[] methods = p.methods;

            for (Method m : methods) {
                // get class and method
                MLog.d(TAG, pairs.getKey() + " = " + m.getName());

                Annotation[] annotations = m.getDeclaredAnnotations();

                for (Annotation annotation2 : annotations) {

                    MLog.d(TAG,
                            annotation2.toString() + " " + annotation2.annotationType()
                                    .getSimpleName() + " " + PhonkMethod.class.getSimpleName()
                    );

                    if (annotation2.annotationType().getSimpleName().equals(PhonkMethod.class.getSimpleName())) {
                        String desc = ((PhonkMethod) annotation2).description();
                        String example = ((PhonkMethod) annotation2).example();
                        MLog.d(TAG, desc);
                    }
                }

            }
            it.remove(); // avoids mContext ConcurrentModificationException
        }

    }

    public String getDocumentation() {
        Gson gson = new Gson();

        return gson.toJson(doc);
    }

    public void clear() {
        apis.clear();
        doc = null;
        apis = new HashMap<>();
        doc = new APIManagerDoc();
    }

    class API {
        public final Class cls;
        public final Method[] methods;

        public API(Class cls, Method[] m) {
            this.cls = cls;
            this.methods = m;
        }
    }

}
