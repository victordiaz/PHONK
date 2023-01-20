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

package io.phonk.gui.projectbrowser.folderlist;

public class FolderAdapterData {
    static final int TYPE_TITLE = 0;
    static final int TYPE_FOLDER_NAME = 1;

    final int itemType;
    final String parentFolder;
    final String name;
    final int numSubfolders;

    public FolderAdapterData(int itemType, String parentFolder, String name) {
        this(itemType, parentFolder, name, -1);
    }

    public FolderAdapterData(int itemType, String parentFolder, String name, int numSubfolders) {
        this.itemType = itemType;
        this.parentFolder = parentFolder;
        this.name = name;
        this.numSubfolders = numSubfolders;
    }
}