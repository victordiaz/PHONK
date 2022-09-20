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

package io.phonk;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.phonk.gui.settings.PhonkSettings;
import io.phonk.runner.base.utils.MLog;

// https://stackoverflow.com/questions/601503/how-do-i-obtain-crash-data-from-my-android-application
public class PhonkExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final java.lang.String TAG = PhonkExceptionHandler.class.getSimpleName();
    private final Thread.UncaughtExceptionHandler defaultUEH;

    private final File localPath;

    /*
     * if any of the parameters is null, the respective functionality
     * will not be used
     */
    public PhonkExceptionHandler() {
        this.localPath = new File(PhonkSettings.getLogsFolder());
        localPath.mkdirs();
        MLog.d(TAG, "exception " + localPath.getAbsolutePath());

        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
    }

    public void uncaughtException(Thread t, Throwable e) {
        SimpleDateFormat s = new SimpleDateFormat("yyyyMMdd_hhmmss");
        String timestamp = s.format(new Date());

        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        String stacktrace = result.toString();
        printWriter.close();
        String filename = timestamp + ".log";

        writeToFile(stacktrace, filename);

        defaultUEH.uncaughtException(t, e);
    }

    private void writeToFile(String stacktrace, String filename) {
        try {
            BufferedWriter bos = new BufferedWriter(new FileWriter(localPath.getAbsoluteFile() + File.separator + filename));
            bos.write(stacktrace);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            MLog.d(TAG, "cannot write log file");
            e.printStackTrace();
        }
    }
}
