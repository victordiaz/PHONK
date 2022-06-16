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

package io.phonk.appinterpreter;

import io.phonk.gui.settings.UserPreferences;


public class Network {

    private final AppRunnerCustom mAppRunner;

    Network(AppRunnerCustom appRunner) {
        this.mAppRunner = appRunner;
    }

    public void checkVersion() {
        //check if new version is available
        if (mAppRunner.pNetwork.isNetworkAvailable() && (boolean) UserPreferences.getInstance().get("notify_new_version")) {
            /*
            mAppRunner.pNetwork.httpGet("http://www.phonk.app/downloads/list_latest.php", data -> {
                //console.log(event + " " + data);
                String[] splitted = ((String) data.get("response")).split(":");
                String remoteFile = "http://www.phonk.app/downloads/" + splitted[0];
                String versionName = splitted[1];
                int versionCode = Integer.parseInt(splitted[2]);

                if (versionCode > mAppRunner.pPhonk.versionCode()) {
                    // TODO enable this
                    mAppRunner.pUi.popupInfo("New version available", "The new version " + versionName + " is available in the phonk.app website. Do you want to get it?", "Yes!", "Later", new PUI.popupCB() {
                        @Override
                        public void event(boolean b) {
                            if (b) {
                                mAppRunner.pDevice.openWebApp("http://www.phonk.app");
                            }
                        }
                    });
                } else {
                    // console.log("updated");
                }
            });
            */
        }
    }
}
