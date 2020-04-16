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

package io.phonk.runner.apprunner;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import io.phonk.runner.base.models.Project;
import io.phonk.runner.base.utils.FileIO;

public class AppRunnerHelper {

    protected static final String TAG = AppRunnerHelper.class.getSimpleName();

    public static String MAIN_FILENAME = "main.js";
    private Project currentProject;

    public static String getProjectPath(Project p) {
        return AppRunnerSettings.getFolderPath(p.getSandboxPath());
    }

    // Get code from sdcard
    public static String getCode(Project p) {
        return getCode(p, AppRunnerSettings.MAIN_FILENAME);
    }

    public static String getCode(Project p, String name) {
        String path = p.getFullPath() + File.separator + name;

        return FileIO.loadStringFromFile(path);
    }

    public static Map<String, Object> readProjectProperties(Context c, Project p) {
        Map<String, Object> map = null;

        String json = getCode(p, "app.conf");
        if (json != null) {
            Gson gson = new Gson();
            Type stringStringMap = new TypeToken<Map<String, Object>>() {}.getType();
            map = gson.fromJson(json, stringStringMap);
        } else {
            map = new HashMap<String, Object>();
        }

        resetValues(map);

        return map;
    }

    public static HashMap<String, Object> createSettings() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        resetValues(map);

        return map;
    }

    private static void resetValues(Map<String, Object> map) {
        // fill with default properties
        if (!map.containsKey("name")) map.put("name", "");
        if (!map.containsKey("description")) map.put("description", "portrait");
        if (!map.containsKey("icon")) map.put("icon", "portrait");
        if (!map.containsKey("window_size")) map.put("window_size", "");
        if (!map.containsKey("orientation")) map.put("orientation", "portrait");
        if (!map.containsKey("screen_mode")) map.put("screen_mode", "normal");
        if (!map.containsKey("featured_image")) map.put("featured_image", "");
        if (!map.containsKey("background_service")) map.put("background_service", false);
        if (!map.containsKey("protocoder_version")) map.put("protocoder_version", "");
        if (!map.containsKey("permissions")) map.put("permissions", "");
        if (!map.containsKey("author")) map.put("author", "");
        if (!map.containsKey("contact")) map.put("contact", "");
        if (!map.containsKey("url")) map.put("url", "");
        if (!map.containsKey("execute_on_save")) map.put("execute_on_save", "");
    }


}
