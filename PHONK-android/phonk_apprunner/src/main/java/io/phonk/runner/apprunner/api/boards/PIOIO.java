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

package io.phonk.runner.apprunner.api.boards;

import io.phonk.runner.apidoc.annotation.PhonkClass;
import io.phonk.runner.apidoc.annotation.PhonkMethod;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.api.ProtoBase;
import io.phonk.runner.apprunner.api.common.ReturnInterface;
import io.phonk.runner.apprunner.api.common.ReturnObject;
import io.phonk.runner.base.hardware.IOIOBoard;
import io.phonk.runner.base.utils.MLog;
import ioio.lib.api.AnalogInput;
import ioio.lib.api.DigitalInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.PwmOutput;
import ioio.lib.api.exception.ConnectionLostException;

@PhonkClass
public class PIOIO extends ProtoBase {

    private final String TAG = PIOIO.class.getSimpleName();

    private IOIOBoard board;
    private IOIO mIoio;
    private ReturnInterface mCallbackConnected;
    private ReturnInterface mCallbackDisconnected;
    private ReturnInterface mCallbackStatus;

    public PIOIO(AppRunner appRunner) {
        super(appRunner);
    }

    @PhonkMethod(description = "initializes ioio board", example = "ioio.connect();")
    public void connect() {
        this.board = new IOIOBoard(getContext(), ioioCallback);
        board.powerOn();
        getAppRunner().whatIsRunning.add(board);
    }

    @PhonkMethod
    public void onConnect(ReturnInterface callback) {
        mCallbackConnected = callback;
    }

    final IOIOBoard.HardwareCallback ioioCallback = new IOIOBoard.HardwareCallback() {
        @Override
        public void onConnect(Object ioio) {
            MLog.d(TAG, "ioio Connected " + ioio);
            PIOIO.this.mIoio = (IOIO) ioio;

            ReturnObject ret = new ReturnObject();
            ret.put("status", "connected");

            mHandler.post(() -> {
                if (mCallbackConnected != null) mCallbackConnected.event(ret);
                if (mCallbackStatus != null) mCallbackStatus.event(ret);
            });
        }

        @Override
        public void setup() {
        }

        @Override
        public void loop() {
            MLog.d(TAG, "loop");
        }

        @Override
        public void onComplete() {
        }
    };

    public IOIO get() {
        return mIoio;
    }

    @PhonkMethod(description = "stops the ioio board", example = "ioio.stop();")
    public void stop() {
        if (mIoio != null) {
            board.powerOff();
            board = null;
            mIoio = null;
        }
    }

    @PhonkMethod(description = "", example = "")
    public DigitalOutput openDigitalOutput(int pinNum) throws ConnectionLostException {
        return mIoio.openDigitalOutput(pinNum, false);
    }

    @PhonkMethod(description = "", example = "")
    public DigitalInput openDigitalInput(int pinNum) throws ConnectionLostException {
        return mIoio.openDigitalInput(pinNum, DigitalInput.Spec.Mode.PULL_UP);
    }

    @PhonkMethod(description = "", example = "")
    public AnalogInput openAnalogInput(int pinNum) throws ConnectionLostException {
        return mIoio.openAnalogInput(pinNum);
    }

    @PhonkMethod(description = "", example = "")
    public PwmOutput openPWMOutput(int pinNum, int freq) throws ConnectionLostException {
        return mIoio.openPwmOutput(pinNum, freq);
    }

    @Override
    public void __stop() {
        stop();
    }
}