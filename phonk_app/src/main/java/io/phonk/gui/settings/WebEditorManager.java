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

package io.phonk.gui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import io.phonk.runner.base.utils.FileIO;
import io.phonk.runner.base.utils.MLog;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class WebEditorManager {

    private static final String TAG = WebEditorManager.class.getSimpleName();
    public static final String DEFAULT = "default";

    private static WebEditorManager instance;

    public static WebEditorManager getInstance() {
        if (instance == null) {
            instance = new WebEditorManager();
        }

        return instance;
    }

    public String[] listEditors() {
        String folderUrl = getBaseDir();
        //MLog.d(TAG, folderUrl);
        ArrayList<String> editors = new ArrayList<String>();
        editors.add("default");
        File dir = null;

        dir = new File(folderUrl);
        if (!dir.exists()) {
            dir.mkdir();
        }

        File[] all_projects = dir.listFiles();
        Arrays.sort(all_projects);

        for (File file : all_projects) {
            if (file.getName().equals(PhonkSettings.APP_FOLDER_CUSTOM_WEBEDITOR) == false) {
                //MLog.d(TAG, file.getName());
                editors.add(file.getName());
            }
        }

        return editors.toArray(new String[editors.size()]);
    }

    public String getCurrentEditor(Context c) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        return prefs.getString("pref_change_editor", "default");
    }

    public String getUrlEditor(Context c) {
        return getBaseDir() + File.separator + getCurrentEditor(c) + File.separator;
    }

    public String getBaseDir() {
        return PhonkSettings.getBaseWebEditorsDir();
    }


    public String getCustomJSInterpreterIfExist(Context c) {
        File file = new File(getUrlEditor(c) + "phonk_js" + File.separator + "custom.js");

        MLog.d("TAG", "trying to load custom js interpreter from " + file.getAbsolutePath());
        String code = "";

        if (file.exists()) {
            code = FileIO.loadStringFromFile(file.getAbsolutePath());
            MLog.d("TAG", "loaded custom js interpreter in " + file.getAbsolutePath() + " " + code);
        } else {
            MLog.d("TAG", "cannot load custom js interpreter ");
        }

        return code;
    }


}
