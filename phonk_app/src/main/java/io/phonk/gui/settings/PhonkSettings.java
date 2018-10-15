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

import android.os.Build;

import java.io.File;

import io.phonk.runner.apprunner.AppRunnerSettings;

public class PhonkSettings extends AppRunnerSettings {

	/*
	 * Protocoder app settings
	 */
    public final static boolean DEBUG                       = true;
    public static String PROTO_FILE_EXTENSION               = ".proto";

    public static final String APP_FOLDER_CUSTOM_WEBEDITOR  = "webeditors";
    public static final String EXPORTED_FOLDER              = "exported";
    public static final String TEMPLATES_FOLDER             = "templates";

    public static int MIN_SUPPORTED_VERSION = Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    public static final String NOTIFICATION_CHANNEL_ID = "io.phonk";

    public static final int WEBSOCKET_PORT                  = 8587;
    public static final int HTTP_PORT                       = 8585;
    public final int FTP_PORT                               = 8589;

    public static String getBaseWebEditorsDir() { return getBaseDir() + APP_FOLDER_CUSTOM_WEBEDITOR + File.separator; }

    public static String getPrefUrl() {
        return getBaseDir() + "settings.conf";
    }
}
