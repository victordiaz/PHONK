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

package io.phonk.runner.apprunner.api.media;

import android.hardware.usb.UsbDevice;

import androidx.annotation.NonNull;

import io.phonk.runner.apidoc.annotation.PhonkClass;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.api.ProtoBase;
import io.phonk.runner.apprunner.api.common.ReturnInterface;
import io.phonk.runner.apprunner.api.common.ReturnObject;
import io.phonk.runner.base.utils.MLog;
import jp.kshoji.driver.midi.device.MidiInputDevice;
import jp.kshoji.driver.midi.device.MidiOutputDevice;
import jp.kshoji.driver.midi.util.UsbMidiDriver;

@PhonkClass
public class PMidi extends ProtoBase {
    private static final String TAG = PMidi.class.getSimpleName();

    private ReturnInterface mConnectionCallback;
    private ReturnInterface mMidiEvent;

    private void callbackData(final String deviceAddress, final int cable, final int channel, final int function, final int value) {
        MLog.d(TAG, "new val + " + cable + " " + channel + " " + function + " " + value);
        mHandler.post(() -> {
            ReturnObject o = new ReturnObject();
            o.put("midiAddress", deviceAddress);
            o.put("cable", cable);
            o.put("channel", channel);
            o.put("function", function);
            o.put("value", value);
            if (mMidiEvent != null) mMidiEvent.event(o);
        });
    }

    private void callbackConnection(final String type, final String attached, final Object midiDevice) {
        mHandler.post(() -> {
            ReturnObject o = new ReturnObject();
            o.put("type", type);
            o.put("status", attached);
            o.put("device", midiDevice);
            if (mConnectionCallback != null) mConnectionCallback.event(o);
        });
    }

    private final UsbMidiDriver usbMidiDriver;

    public PMidi(AppRunner appRunner) {
        super(appRunner);

        usbMidiDriver = new UsbMidiDriver(appRunner.getAppContext()) {

            @Override
            public void onMidiMiscellaneousFunctionCodes(@NonNull MidiInputDevice midiInputDevice, int i, int i1, int i2, int i3) {

            }

            @Override
            public void onMidiCableEvents(@NonNull MidiInputDevice midiInputDevice, int i, int i1, int i2, int i3) {

            }

            @Override
            public void onMidiSystemCommonMessage(@NonNull MidiInputDevice midiInputDevice, int i, byte[] bytes) {

            }

            @Override
            public void onMidiSystemExclusive(@NonNull MidiInputDevice midiInputDevice, int i, byte[] bytes) {

            }

            @Override
            public void onMidiNoteOff(MidiInputDevice midiInputDevice, int cable, int channel, int note, int velocity) {
                callbackData(midiInputDevice.getDeviceAddress(), cable, channel, note, velocity);
            }

            @Override
            public void onMidiNoteOn(MidiInputDevice midiInputDevice, int cable, int channel, int note, int velocity) {
                callbackData(midiInputDevice.getDeviceAddress(), cable, channel, note, velocity);
            }

            @Override
            public void onMidiPolyphonicAftertouch(@NonNull MidiInputDevice midiInputDevice, int cable, int channel, int note, int pressure) {
                callbackData(midiInputDevice.getDeviceAddress(), cable, channel, note, pressure);
            }

            @Override
            public void onMidiControlChange(@NonNull MidiInputDevice midiInputDevice, int cable, int channel, int function, int value) {
                callbackData(midiInputDevice.getDeviceAddress(), cable, channel, function, value);
            }

            @Override
            public void onMidiProgramChange(@NonNull MidiInputDevice midiInputDevice, int cable, int channel, int program) {
                callbackData(midiInputDevice.getDeviceAddress(), cable, channel, channel, program);
            }

            @Override
            public void onMidiChannelAftertouch(@NonNull MidiInputDevice midiInputDevice, int cable, int channel, int pressure) {
                callbackData(midiInputDevice.getDeviceAddress(), cable, channel, channel, pressure);
            }

            @Override
            public void onMidiPitchWheel(@NonNull MidiInputDevice midiInputDevice, int cable, int channel, int amount) {
                callbackData(midiInputDevice.getDeviceAddress(), cable, channel, channel, amount);
            }

            @Override
            public void onMidiSingleByte(@NonNull MidiInputDevice midiInputDevice, int i, int i1) {

            }

            @Override
            public void onMidiTimeCodeQuarterFrame(@NonNull MidiInputDevice midiInputDevice, int i, int i1) {

            }

            @Override
            public void onMidiSongSelect(@NonNull MidiInputDevice midiInputDevice, int i, int i1) {

            }

            @Override
            public void onMidiSongPositionPointer(@NonNull MidiInputDevice midiInputDevice, int i, int i1) {

            }

            @Override
            public void onMidiTuneRequest(@NonNull MidiInputDevice midiInputDevice, int i) {

            }

            @Override
            public void onMidiTimingClock(@NonNull MidiInputDevice midiInputDevice, int i) {

            }

            @Override
            public void onMidiStart(@NonNull MidiInputDevice midiInputDevice, int i) {

            }

            @Override
            public void onMidiContinue(@NonNull MidiInputDevice midiInputDevice, int i) {

            }

            @Override
            public void onMidiStop(@NonNull MidiInputDevice midiInputDevice, int i) {

            }

            @Override
            public void onMidiActiveSensing(@NonNull MidiInputDevice midiInputDevice, int i) {

            }

            @Override
            public void onMidiReset(@NonNull MidiInputDevice midiInputDevice, int i) {

            }

            @Override
            public void onMidiRPNReceived(@NonNull MidiInputDevice midiInputDevice, int i, int i1, int i2, int i3) {

            }

            @Override
            public void onMidiNRPNReceived(@NonNull MidiInputDevice midiInputDevice, int i, int i1, int i2, int i3) {

            }

            @Override
            public void onDeviceDetached(@NonNull UsbDevice usbDevice) {

            }

            @Override
            public void onMidiInputDeviceDetached(@NonNull MidiInputDevice midiInputDevice) {
                callbackConnection("input", "detached", midiInputDevice);
            }

            @Override
            public void onMidiOutputDeviceDetached(@NonNull MidiOutputDevice midiOutputDevice) {
                // Toast.makeText(getContext(), "USB MIDI Output Device deatached" + midiOutputDevice.getUsbDevice().getDeviceName(), Toast.LENGTH_LONG).show();
                callbackConnection("output", "detached", midiOutputDevice);
            }

            @Override
            public void onDeviceAttached(@NonNull UsbDevice usbDevice) {
                // callbackConnection("device", "attached", midiOutputDevice);
            }

            @Override
            public void onMidiInputDeviceAttached(@NonNull MidiInputDevice midiInputDevice) {
                callbackConnection("input", "attached", midiInputDevice);
            }

            @Override
            public void onMidiOutputDeviceAttached(MidiOutputDevice midiOutputDevice) {
                // Toast.makeText(getContext(), "USB MIDI Output Device " + midiOutputDevice.getUsbDevice().getDeviceName() + " has been attached.", Toast.LENGTH_LONG).show();

                callbackConnection("output", "attached", midiOutputDevice);
            }
        };

        usbMidiDriver.open();
    }

    public PMidi onDeviceEvent(ReturnInterface callback) {
        mConnectionCallback = callback;

        return this;
    }

    public PMidi onMidiEvent(final ReturnInterface callbackfn) {
        mMidiEvent = callbackfn;

        return this;
    }


    @Override
    public void __stop() {
        MLog.d(TAG, "close");
        // if (mMidiOutputDevice != null) mMidiOutputDevice.suspend();
        mMidiEvent = null;
        usbMidiDriver.close();
    }


}
