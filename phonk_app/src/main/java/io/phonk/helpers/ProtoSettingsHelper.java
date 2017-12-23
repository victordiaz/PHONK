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

package io.phonk.helpers;

import android.content.Context;

import io.phonk.gui.settings.PhonkSettings;
import io.phonk.runner.base.utils.FileIO;

import java.io.File;

public class ProtoSettingsHelper {

    private static String PHONK_EXTENSION = ".phonk";

    public interface InstallListener {
        void onReady();
    }

    public static void installExamples(final Context c, final String assetsName, final InstallListener l) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                File dir = new File(PhonkSettings.getBaseDir() + assetsName);
                FileIO.deleteDir(dir);
                FileIO.copyFileOrDir(c, assetsName);
                l.onReady();
            }
        }).start();
    }

}
