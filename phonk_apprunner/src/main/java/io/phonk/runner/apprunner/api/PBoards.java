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

package io.phonk.runner.apprunner.api;

import io.phonk.runner.apidoc.annotation.PhonkMethod;
import io.phonk.runner.apidoc.annotation.PhonkMethodParam;
import io.phonk.runner.apidoc.annotation.PhonkObject;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.api.boards.PIOIO;
import io.phonk.runner.apprunner.api.boards.PSerial;
import io.phonk.runner.apprunner.api.common.ReturnInterface;

@PhonkObject
public class PBoards extends ProtoBase {

    private final String TAG = PBoards.class.getSimpleName();

    public PBoards(AppRunner appRunner) {
        super(appRunner);
    }


    @PhonkMethod(description = "initializes the ioio board", example = "")
    public PIOIO connectIOIO() {
        PIOIO ioio = new PIOIO(getAppRunner());
        return ioio;
    }

    @PhonkMethod(description = "initializes serial communication", example = "")
    public PSerial createSerial(int bauds) {
        PSerial serial = new PSerial(getAppRunner());
        return serial;
    }

    @Override
    public void __stop() {

    }
}