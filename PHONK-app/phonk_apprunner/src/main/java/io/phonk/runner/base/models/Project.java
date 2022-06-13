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

package io.phonk.runner.base.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;

import io.phonk.runner.apprunner.AppRunnerSettings;
import io.phonk.runner.base.utils.MLog;

public class Project {
    private final String TAG = Project.class.getSimpleName();

    // we need this to serialize the data using intent bundles
    //public final static String TYPE = "projectType";
    public static final String FOLDER = "projectFolder";
    public static final String NAME = "projectName";
    public static final String URL = "projectUrl";
    public static final String PREFIX = "prefix";
    public static final String INTENTCODE = "code";
    public static final String POSTFIX = "postfix";
    public static final String LAUNCH_DATA = "launchData";

    public static final String SETTINGS_SCREEN_ALWAYS_ON = "settings_screenOn";
    public static final String SETTINGS_SCREEN_WAKEUP = "settings_wakeUpScreen";
    public static final String SETTINGS_SCREEN_ORIENTATION = "settings_screenOrientation";
    public static final String SERVER_PORT = "settings_phonk_port";
    public static final String SETTINGS = "project_settings";
    public static final String DEVICE_ID = "device_id";

    /*
     * this will get serialized
     */
    public String name;
    public String folder;
    public String parentFolder;

    public Project() {

    }

    public Project(String folder, String projectName) {
        this.folder = folder;
        this.name = projectName;
    }

    public Project(String script) {
        String[] path = script.split("/");
        this.parentFolder = path[0];
        this.folder = path[0] + "/" + path[1];
        this.name = path[2];

        MLog.d(TAG, "rename" + path[0] + " - " + path[1]);
    }

    public String getName() {
        return this.name;
    }

    // returns something like playground/User Projects
    public String getFolder() {
        return this.folder;
    }

    public String geFoldertPath() {
        return AppRunnerSettings.getBaseDir() + this.folder + File.separator;
    }

    public String getSandboxPath() {
        return this.folder + File.separator + this.name + File.separator;
    }

    public String getSandboxPathParent() {
        return this.folder + File.separator;
    }

    public String getSandboxPathForFile(String filename) {
        return getSandboxPath() + filename;
    }

    public String getParentPath() {
        return AppRunnerSettings.getBaseDir() + this.folder + File.separator;
    }

    public String getParentParentFullPath() { return AppRunnerSettings.getBaseDir() + this.parentFolder; }

    public String getFullPath() {
        return AppRunnerSettings.getBaseDir() + getSandboxPath();
    }

    public String getFullPathForFile(String fileName) {
        return getFullPath() + fileName;
    }

    public boolean exists() {
        File f = new File(getFullPath());
        return f.exists();
    }

    public String getIconUrl() {
        File f = new File(this.getFullPathForFile("icon.png"));
        if (f.exists()) return f.toString();
        else return null;
    }

    public Bitmap getIcon() {
        String iconUrl = getIconUrl();

        if (iconUrl != null) {
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(iconUrl, bmOptions);
            bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
            return bitmap;
        } else {
            return null;
        }

    }
}
