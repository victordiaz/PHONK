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

import static android.content.Context.MIDI_SERVICE;

import android.content.pm.PackageManager;
import android.media.midi.MidiDevice;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiInputPort;
import android.media.midi.MidiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import io.phonk.runner.apidoc.annotation.PhonkClass;
import io.phonk.runner.apidoc.annotation.PhonkMethod;
import io.phonk.runner.apidoc.annotation.PhonkMethodParam;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.api.ProtoBase;
import io.phonk.runner.base.utils.MLog;

@RequiresApi(api = Build.VERSION_CODES.M)
@PhonkClass
public class PMidiController extends ProtoBase {
    private static final String TAG = PMidiController.class.getSimpleName();

    private static final byte STATUS_NOTE_OFF = (byte) 0x80;
    private static final byte STATUS_NOTE_ON = (byte) 0x90;
    private static final byte STATUS_PITCH_BEND = (byte) 0xE0;
    private static final int DISABLE_PITCH_BEND_VALUE = 8191;  // max pitch bend (=16383) / 2
    private final Map<String, MidiInput> midiInputByIds = new TreeMap<>();
    private final Map<Integer, MidiDeviceInfo> midiDeviceInfoByIds = new HashMap<>();
    private MidiManager midiManager;
    private MidiInputPort midiInputPort;
    private MidiDevice midiDevice;

    public PMidiController(final AppRunner appRunner) {
        super(appRunner);
        if (getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_MIDI)) {
            midiManager = (MidiManager) getContext().getSystemService(MIDI_SERVICE);
            if (midiManager != null) {
                return;
            }
        }
        Toast.makeText(getContext(), "MIDI not supported!", Toast.LENGTH_LONG).show();
    }

    @PhonkMethod(description = "Find available midi inputs to send midi command to", example = "")
    @PhonkMethodParam(params = "")
    public MidiInput[] findAvailableMidiInputs() {
        midiInputByIds.clear();
        midiDeviceInfoByIds.clear();
        for (final MidiDeviceInfo midiDeviceInfo : midiManager.getDevices()) {
            midiDeviceInfoByIds.put(midiDeviceInfo.getId(), midiDeviceInfo);
            final int inputPortCount = midiDeviceInfo.getInputPortCount();
            for (int portNumber = 0; portNumber < inputPortCount; portNumber++) {
                final MidiInput midiInput = new MidiInput(midiDeviceInfo, portNumber);
                midiInputByIds.put(midiInput.getDeviceInputId(), midiInput);
            }
        }
        return midiInputByIds.values().toArray(new MidiInput[0]);
    }

    @PhonkMethod(description = "Find available midi inputs to send midi command to", example = "")
    @PhonkMethodParam(params = "midiInputId fetched from findAvailableMidiInputs()")
    public PMidiController setupMidi(final String midiInputId) {
        final MidiInput midiInput = midiInputByIds.get(midiInputId);
        if (midiInput != null) {
            final MidiDeviceInfo midiDeviceInfo = midiDeviceInfoByIds.get(midiInput.getDeviceId());
            midiManager.openDevice(midiDeviceInfo, midiDevice -> {
                this.midiDevice = midiDevice;
                if (midiDevice == null) {
                    Log.e(TAG, "could not open device " + midiInput);
                } else {
                    midiInputPort = midiDevice.openInputPort(midiInput.getPortNumber());
                }
            }, new Handler(Looper.getMainLooper()));
        } else {
            Toast.makeText(getContext(), "Unknown MIDI input id: " + midiInputId, Toast.LENGTH_LONG).show();
        }
        return this;
    }

    @PhonkMethod(description = "Stop playing a note", example = "")
    @PhonkMethodParam(params = "channel [0-15], pitch[0-127], velocity[0-127]")
    public void noteOff(final int channel, final int pitch, final int velocity) {
        midiCommand(STATUS_NOTE_OFF + channel, pitch, velocity);
    }

    @PhonkMethod(description = "Sends a midi message you build yourself. Quite low level!", example = "")
    @PhonkMethodParam(params = "status [0-127], data1[0-127], data2[0-127]")
    public void midiCommand(final int status, final int data1, final int data2) {
        final byte[] mByteBuffer = new byte[3];
        mByteBuffer[0] = (byte) status;
        mByteBuffer[1] = (byte) data1;
        mByteBuffer[2] = (byte) data2;
        long now = System.nanoTime();
        midiSend(mByteBuffer, now);
    }

    private void midiSend(final byte[] buffer, final long timestamp) {
        try {
            if (midiInputPort != null) {
                midiInputPort.send(buffer, 0, buffer.length, timestamp);
            }
        } catch (IOException e) {
            Log.e(TAG, "midiSend failed " + e);
        }
    }

    @PhonkMethod(description = "Plays a note", example = "")
    @PhonkMethodParam(params = "channel [0-15], pitch[0-127], velocity[0-127]")
    public void noteOn(final int channel, final int pitch, final int velocity) {
        midiCommand(STATUS_NOTE_ON + channel, pitch, velocity);
    }

    @PhonkMethod(description = "Disable pitch bend", example = "")
    @PhonkMethodParam(params = "channel [0-15]")
    public void stopPitchBend(final int channel) {
        this.pitchBend(channel, DISABLE_PITCH_BEND_VALUE);
    }

    @PhonkMethod(description = "Pitch bend notes", example = "")
    @PhonkMethodParam(params = "channel [0-15], pitchBendValue[0-16383] with 8192 being no pitch bend")
    public void pitchBend(final int channel, final int pitchBendValue) {
        // need to convert pitchBendValue in lsb 7 bytes and msb 7 bytes
        int pitchBendValueMsbOn7Bytes = pitchBendValue >> 7;
        int pitchBendValueLsbOn7Bytes = pitchBendValue & 0x7F;
        // careful, lsb is first!
        midiCommand(STATUS_PITCH_BEND + channel, pitchBendValueLsbOn7Bytes, pitchBendValueMsbOn7Bytes);
    }

    @Override
    public void __stop() {
        MLog.i(TAG, "close");
        try {
            if (midiInputPort != null) {
                midiInputPort.close();
            }
            midiInputPort = null;
            if (midiDevice != null) {
                midiDevice.close();
            }
            midiDevice = null;
        } catch (IOException e) {
            Log.e(TAG, "midi cleanup failed", e);
        }
    }
}
