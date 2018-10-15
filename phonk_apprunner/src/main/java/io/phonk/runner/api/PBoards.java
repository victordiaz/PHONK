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

package io.phonk.runner.api;

import io.phonk.runner.api.boards.PArduino;
import io.phonk.runner.api.boards.PIOIO;
import io.phonk.runner.api.boards.PSerial;
import io.phonk.runner.apidoc.annotation.ProtoMethod;
import io.phonk.runner.apidoc.annotation.ProtoMethodParam;
import io.phonk.runner.apidoc.annotation.ProtoObject;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.base.hardware.AdkPort;
import io.phonk.runner.base.utils.MLog;

@ProtoObject
public class PBoards extends ProtoBase {

    private final String TAG = PBoards.class.getSimpleName();

    public PBoards(AppRunner appRunner) {
        super(appRunner);
    }


    @ProtoMethod(description = "initializes the ioio board", example = "")
    @ProtoMethodParam(params = {"function()"})
    public PIOIO connectIOIO(PIOIO.startCB callbackfn) {
        PIOIO ioio = new PIOIO(getAppRunner());
        ioio.start(callbackfn);

        return ioio;
    }

    @ProtoMethod(description = "initializes serial communication", example = "")
    @ProtoMethodParam(params = {"bauds", "function()"})
    public PSerial createSerial(int bauds) {
        PSerial serial = new PSerial(getAppRunner(), bauds);

        return serial;
    }

    @ProtoMethod(description = "initializes arduino board without callback", example = "")
    @ProtoMethodParam(params = {""})
    public PArduino connectArduino() {
        PArduino arduino = new PArduino(getAppRunner());
        arduino.start();

        return arduino;
    }


    @ProtoMethod(description = "initializes arduino board with callback", example = "")
    @ProtoMethodParam(params = {"bauds", "function()"})
    public PArduino connectArduino(int bauds, String endline, PArduino.onReadCB callbackfn) {
        PArduino arduino = new PArduino(getAppRunner());
        arduino.start(bauds, endline, callbackfn);

        return arduino;
    }


    @ProtoMethod(description = "initializes adk boards with callback", example = "")
    @ProtoMethodParam(params = {"bauds", "function()"})
    public AdkPort startADK(PArduino.onReadCB callbackfn) {
        final AdkPort adk = new AdkPort(getContext());


        Thread thread = new Thread(adk);
        thread.start();

        String[] list = adk.getList(getContext());
        for (int i = 0; i < list.length; i++) {
            // writeToConsole(list[i] + "\n\r");
        }

        adk.attachOnNew(new AdkPort.MessageNotifier() {
            @Override
            public void onNew() {
                int av = adk.available();
                byte[] buf = adk.readB();

                String toAdd = new String(buf, 0, av);
                MLog.d(TAG, "Received:" + toAdd);
            }
        });

        return adk;
    }

    @Override
    public void __stop() {

    }
}