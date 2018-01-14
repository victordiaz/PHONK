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

package io.phonk.server;

import android.content.Context;

import io.phonk.appinterpreter.AppRunnerCustom;
import io.phonk.gui.settings.PhonkSettings;
import io.phonk.gui.settings.UserPreferences;
import io.phonk.runner.api.network.PFtpServer;
import io.phonk.runner.base.utils.MLog;

public class PhonkFtpServer extends PFtpServer {
    public static final String TAG = PhonkFtpServer.class.getSimpleName();

    private static PhonkFtpServer instance = null;
    private static boolean started = false;
    private Context c;

    public PhonkFtpServer(AppRunnerCustom appRunner, int port) {
        super(port, null);
        MLog.d(TAG, "" + port);

        c = appRunner.getAppContext();
        UserPreferences newUserPreferences = UserPreferences.getInstance();

        String user = (String) newUserPreferences.get("ftp_user");
        String password = (String) newUserPreferences.get("ftp_password");

        MLog.d(TAG, "" + user + " " + password);
        addUser(user, password, PhonkSettings.getBaseDir(), true);
    }

    public void stopServer() {
        instance = null;

        if (instance != null) {
            stop();
            started = false;
        }
    }

    public void startServer() {
        if (!started) {
            start();
            started = true;
        }
    }

    public boolean isStarted() {
        return started;
    }
}
