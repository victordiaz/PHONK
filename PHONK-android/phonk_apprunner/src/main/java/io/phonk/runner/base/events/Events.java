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

package io.phonk.runner.base.events;

import io.phonk.runner.base.models.Project;

public class Events {
    public static final String KEY = "key";
    public static final String SMS = "sms";
    public static final String NFC_READ = "nfc_read";
    public static final String NFC_WRITTEN = "nfc_written";
    public static final String BLUETOOTH = "bluetooth";
    public static final String VOICE_RECOGNITION = "voice_recognition";


    public static class ProjectEvent {
        private final Project project;
        private final String action;

        public ProjectEvent(String action, Project project) {
            this.action = action;
            this.project = project;
        }

        public ProjectEvent(String action, String folder, String name) {
            this.action = action;
            this.project = new Project(folder, name);
        }

        public String getAction() {
            return action;
        }

        public Project getProject() {
            return project;
        }
    }


    public static class LogEvent {
        private final String action;
        private final String time;
        private final String data;

        public LogEvent(String action, String time, String data) {
            this.action = action;
            this.time = time;
            this.data = data;
        }

        public String getAction() {
            return action;
        }

        public String getData() {
            return data;
        }

        public String getTime() {
            return time;
        }

    }


}