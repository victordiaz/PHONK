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

import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

public class AppRunnerSettings {

    public static boolean DEBUG = false;

    public static String MAIN_FILENAME                      = "main.js";
    public static final String CONF_FILENAME                = "app.conf";
    public final static String PHONK_FOLDER = "phonk_io";
    public static final String USER_PROJECTS_FOLDER = "playground";
    public static final String EXAMPLES_FOLDER = "examples";
    public static final String LOGS_FOLDER = "logs";

    public static final String LIBRARIES_FOLDER = "libraries";
    public static int MIN_SUPPORTED_VERSION = Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    public static int animGeneralSpeed = 500;
    public static int SERVER_PORT;

    public String id;

    public static String getBaseDir() {
        try {
            return Environment.getExternalStorageDirectory().getCanonicalPath()
                    + File.separator + PHONK_FOLDER + File.separator;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getFolderPath(String folder) {
        return getBaseDir() + folder + File.separator;
    }

    public static String getBaseLibrariesDir() {
        return getBaseDir() + LIBRARIES_FOLDER + File.separator;
    }

    public static String getLogsFolder() {
        return getBaseDir() + LOGS_FOLDER + File.separator;
    }
}
