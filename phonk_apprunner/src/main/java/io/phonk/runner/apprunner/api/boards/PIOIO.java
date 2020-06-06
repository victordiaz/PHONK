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
import io.phonk.runner.apidoc.annotation.PhonkMethodParam;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.api.ProtoBase;
import io.phonk.runner.base.hardware.IOIOBoard;
import io.phonk.runner.base.utils.MLog;
import ioio.lib.api.AnalogInput;
import ioio.lib.api.DigitalInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.PwmOutput;
import ioio.lib.api.exception.ConnectionLostException;

@PhonkClass
public class PIOIO extends ProtoBase implements IOIOBoard.HardwareCallback {

    private final String TAG = PIOIO.class.getSimpleName();

    private IOIOBoard board;
    boolean mIoioStarted = false;
    private IOIO mIoio;
    private startCB mIoioCallbackfn;

    public PIOIO(AppRunner appRunner) {
        super(appRunner);
    }

    // --------- getRequest ---------//
    public interface startCB {
        void event();
    }


    @PhonkMethod(description = "initializes ioio board", example = "ioio.start();")
    @PhonkMethodParam(params = {""})
    public void start() {
        if (!mIoioStarted) {
            this.board = new IOIOBoard(getContext(), this);
            board.powerOn();
            getAppRunner().whatIsRunning.add(board);
        }
    }


    @PhonkMethod(description = "initializes ioio board", example = "ioio.start();")
    @PhonkMethodParam(params = {"function()"})
    public void start(startCB callbackfn) {
        mIoioCallbackfn = callbackfn;
        if (!mIoioStarted) {
            this.board = new IOIOBoard(getContext(), this);
            board.powerOn();
            getAppRunner().whatIsRunning.add(board);
        }
    }

    public IOIO get() {
        return mIoio;
    }


    @PhonkMethod(description = "stops the ioio board", example = "ioio.stop();")
    public void stop() {
        mIoioStarted = false;
        board.powerOff();
        board = null;
    }


    @PhonkMethod(description = "", example = "")
    @PhonkMethodParam(params = {"pinNumber"})
    public DigitalOutput openDigitalOutput(int pinNum) throws ConnectionLostException {
        return mIoio.openDigitalOutput(pinNum, false); // start with the on board

    }


    @PhonkMethod(description = "", example = "")
    @PhonkMethodParam(params = {"pinNumber"})
    public DigitalInput openDigitalInput(int pinNum) throws ConnectionLostException {
        return mIoio.openDigitalInput(pinNum, DigitalInput.Spec.Mode.PULL_UP);

    }


    @PhonkMethod(description = "", example = "")
    @PhonkMethodParam(params = {"pinNumber"})
    public AnalogInput openAnalogInput(int pinNum) throws ConnectionLostException {
        return mIoio.openAnalogInput(pinNum);

    }


    @PhonkMethod(description = "", example = "")
    @PhonkMethodParam(params = {"pinNumber", "frequency"})
    public PwmOutput openPWMOutput(int pinNum, int freq) throws ConnectionLostException {
        return mIoio.openPwmOutput(pinNum, freq);
    }


    public void resume() {
    }

    public void pause() {
    }


    @PhonkMethod(description = "returns true is the ioio board is connected", example = "")
    public boolean isStarted() {
        return mIoioStarted;
    }


    @Override
    public void onConnect(Object obj) {
        this.mIoio = (IOIO) obj;
        MLog.d(TAG, "MOIO Connected");

        if (mIoioCallbackfn != null) {
            mIoioCallbackfn.event();
        }

        mIoioStarted = true;
        mHandler.post(() -> {
        });
    }

    @Override
    public void setup() {
    }

    @Override
    public void loop() {
    }

    @Override
    public void onComplete() {

    }

    @Override
    public void __stop() {
        stop();
    }
}