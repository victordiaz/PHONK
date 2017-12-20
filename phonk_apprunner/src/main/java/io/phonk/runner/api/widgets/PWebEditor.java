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

package io.phonk.runner.api.widgets;

import io.phonk.runner.api.ProtoBase;
import io.phonk.runner.apidoc.annotation.ProtoMethod;
import io.phonk.runner.apidoc.annotation.ProtoMethodParam;
import io.phonk.runner.apprunner.AppRunner;

public class PWebEditor extends ProtoBase {

    public PWebEditor(AppRunner appRunner) {
        super(appRunner);

    }

    //TODO this is mContext place holder

    @ProtoMethod(description = "Loads a Html file in the webIde sidebar", example = "")
    @ProtoMethodParam(params = {"boolean"})
    public void loadHTMLonSideBar(boolean visible) {

    }

    //TODO this is mContext place holder

    @ProtoMethod(description = "Shows/Hides the webIde sidebar", example = "")
    @ProtoMethodParam(params = {"boolean"})
    public void showSideBar(boolean visible) {

    }


    @ProtoMethod(description = "Execute custom js in the webIde", example = "")
    @ProtoMethodParam(params = {"jsText"})
    public void sendJs(String js) {
        //TODO change to events
        //IDEcommunication.getInstance(getContext()).sendCustomJs(js);
    }

    @Override
    public void __stop() {

    }
}