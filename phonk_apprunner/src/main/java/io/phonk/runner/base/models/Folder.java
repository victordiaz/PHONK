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

package io.phonk.runner.common.models;

public class Folder {

    public static final int PARENT = 1;

    private final String folderUrl;
    private String name;
    private String folder;
    private int parent;

    public Folder(String folderUrl, String folder, String projectName) {
        this.folderUrl = folderUrl;
        this.folder = folder;
        this.name = projectName;
    }

    public String getName() {
        return this.name;
    }

    public String getFolder() {
        return this.folder;
    }

    public String getFolderUrl() {
        return this.folderUrl;
    }

    public void setParent() {
        this.parent = PARENT;
    }

    public int getType() {
        return parent;
    }
}
