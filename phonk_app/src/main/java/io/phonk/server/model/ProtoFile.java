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

package io.phonk.server.model;

import java.util.ArrayList;

/**
 * Created by victornomad on 3/03/16.
 */
public class ProtoFile {
    public String project_parent;
    public String name;
    public String path;
    public String type;
    public String code = null;
    public Long size = null;
    public ArrayList<ProtoFile> files = null;
    public boolean isDir;

    public ProtoFile(String path, String name) {
        this.name = name;
        this.path = path;
    }

    public ProtoFile() {

    }

    public String getPath() {
        return path;
    }

    public String getFullPath() {
        return path + name;
    }
}
