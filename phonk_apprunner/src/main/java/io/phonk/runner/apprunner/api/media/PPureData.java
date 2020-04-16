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

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import org.json.JSONArray;
import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.android.utils.PdUiDispatcher;
import org.puredata.core.PdBase;
import org.puredata.core.PdListener;
import org.puredata.core.utils.PdDispatcher;

import java.io.IOException;
import java.util.Arrays;

import io.phonk.runner.apidoc.annotation.PhonkMethod;
import io.phonk.runner.apidoc.annotation.PhonkMethodParam;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.api.ProtoBase;
import io.phonk.runner.apprunner.api.common.ReturnInterface;
import io.phonk.runner.apprunner.api.common.ReturnObject;
import io.phonk.runner.base.utils.MLog;

public class PPureData extends ProtoBase {

    private String TAG = PPureData.class.getSimpleName();

    public String path;
    private int mPatch;
    private ReturnInterface callback;
    private String mFileName;

    public PPureData(AppRunner appRunner) {
        super(appRunner);
    }

    public void loadPatch(String fileName) {
        mFileName = fileName;
    }

    /* this is how we initialize Pd */
    public void start() {
        // load patch
        try {
            MLog.d(TAG, "" + mPatch + " " + mFileName);
            mPatch = PdBase.openPatch(getAppRunner().getProject().getFullPathForFile(mFileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        int srate = PdBase.suggestSampleRate();
        if (srate < 0) srate = AudioParameters.suggestSampleRate();

        int nic = PdBase.suggestInputChannels();
        if (nic < 0) nic = AudioParameters.suggestInputChannels();

        int noc = PdBase.suggestOutputChannels();
        if (noc < 0) noc = AudioParameters.suggestOutputChannels();

        float millis = -1;
        if (millis < 0) millis = 50.0f;  // conservative choice
        int tpb = (int) (0.001f * millis * srate / PdBase.blockSize()) + 1;

        try {
            PdAudio.initAudio(srate, nic, noc, tpb, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* here is where we bind the print statement catcher defined below */
        PdBase.setReceiver(receiver);

        /*
         * here we are adding the listener for various messages
         * from Pd sent to "GUI", i.e., anything that goes into the object
         * [s GUI] will send to the listener defined below
         */
        startAudio();
        getAppRunner().whatIsRunning.add(this);
    }


    public void startAudio() {
        PdAudio.startAudio(getContext());
    }

    public void stopAudio() {
        PdAudio.stopAudio();
    }

    public void stop() {
        PdBase.closePatch(mPatch);
        myDispatcher.release();
        stopAudio();
        PdAudio.release();
        PdBase.release();
    }


    /* We'll use this to catch print statements from Pd
       when the user has a [print] object */
    private final PdDispatcher myDispatcher = new PdUiDispatcher() {
        @Override
        public void print(String s) {
            // MLog.i(TAG, "Pd print " + "" + s);
        }
    };

    /* We'll use this to listen out for messages from Pd.
       Later we'll hook this up to a named receiver. */
    private final PdListener myListener = new PdListener() {
        @Override
        public void receiveMessage(String source, String symbol, Object... args) {
            MLog.i(TAG, "receiveMessage symbol: " + symbol);
            for (Object arg: args) {
                MLog.i(TAG, "receiveMessage atom: " +  arg.toString());
            }
        }

        /* What to do when we receive a list from Pd. In this example
           we're collecting the list from Pd and outputting each atom */
        @Override
        public void receiveList(String source, Object... args) {
            for (Object arg: args) {
                MLog.i(TAG, "receiveList atom: " + arg.toString());
            }
        }

        /* When we receive a symbol from Pd */
        @Override public void receiveSymbol(String source, String symbol) {
            MLog.i(TAG, "receiveSymbol " + symbol);
        }
        /* When we receive a float from Pd */
        @Override public void receiveFloat(String source, float x) {
            MLog.i(TAG, "receiveFloat " + x);
        }
        /* When we receive a bang from Pd */
        @Override public void receiveBang(String source) {
            MLog.i(TAG, "receiveBang " + "bang!");
        }
    };

    // TODO Activate listeners

    PdUiDispatcher receiver = new PdUiDispatcher() {

        public void sendBack(final ReturnObject o) {
            if (callback != null) {
                //mHandler.post(new Runnable() {
                //    @Override
                //    public void run() {
                callback.event(o);
                //    }
                //});
            }
        }

        @Override
        public void print(String s) {
            MLog.d(TAG, "pd >> " + s);

            final ReturnObject o = new ReturnObject();
            o.put("type", "print");
            o.put("value", s);
            sendBack(o);
        }

        @Override
        public void receiveBang(String source) {
            MLog.d(TAG, "bang");

            ReturnObject o = new ReturnObject();
            o.put("type", "bang");
            o.put("source", source);

            sendBack(o);
        }

        @Override
        public void receiveFloat(String source, float x) {
            MLog.d(TAG, "float: " + x);

            ReturnObject o = new ReturnObject();
            o.put("type", "float");
            o.put("source", source);
            o.put("value", x);

            sendBack(o);
        }

        @Override
        public void receiveList(String source, Object... args) {
            MLog.d(TAG, "list: " + Arrays.toString(args));

            JSONArray jsonArray = new JSONArray();
            for (Object arg : args) {
                jsonArray.put(arg);
            }

            ReturnObject o = new ReturnObject();
            o.put("type", "list");
            o.put("source", source);
            o.put("value", jsonArray);

            sendBack(o);
        }

        @Override
        public void receiveMessage(String source, String symbol, Object... args) {
            MLog.d(TAG, "message: " + Arrays.toString(args));

            JSONArray jsonArray = new JSONArray();
            for (Object arg : args) {
                jsonArray.put(arg);
            }

            ReturnObject o = new ReturnObject();
            o.put("type", "message");
            o.put("source", source);
            o.put("value", jsonArray);

            sendBack(o);
        }

        @Override
        public void receiveSymbol(String source, String symbol) {
            MLog.d(TAG, "symbol: " + symbol);

            ReturnObject o = new ReturnObject();
            o.put("type", "symbol");
            o.put("source", source);
            o.put("value", symbol);

            sendBack(o);
        }
    };

    public PPureData onNewData(final ReturnInterface callbackfn) {
        this.callback = callbackfn;
        return this;
    }

    public void listenTo(String m) {
        receiver.addListener(m, null);
    }

    public void removeListener(String m) {
        receiver.removeListener(m, null);
    }

    @PhonkMethod(description = "Sends a message to PdLib", example = "")
    @PhonkMethodParam(params = {"recv", "value"})
    public void sendMessage(String recv, String value) {
        PdBase.sendMessage(recv, value);
    }

    @PhonkMethod(description = "Sends a symbol to PdLib", example = "")
    @PhonkMethodParam(params = {"recv", "value"})
    public void sendSymbol(String recv, String value) {
        PdBase.sendSymbol(recv, value);
    }

    @PhonkMethod(description = "Sends a bang to PdLib", example = "")
    @PhonkMethodParam(params = {"name"})
    public void sendBang(String name) {
        PdBase.sendBang(name);
    }

    @PhonkMethod(description = "Sends a float number to PdLib", example = "")
    @PhonkMethodParam(params = {"name", "value"})
    public void sendFloat(String name, int value) {
        PdBase.sendFloat(name, value);
    }

    @PhonkMethod(description = "Sends a note to PdLib", example = "")
    @PhonkMethodParam(params = {"channel", "pitch, velocity"})
    public void sendNoteOn(int channel, int pitch, int velocity) {
        PdBase.sendNoteOn(channel, pitch, velocity);
    }

    @PhonkMethod(description = "Sends a midibyte to PdLib", example = "")
    @PhonkMethodParam(params = {"port", "value"})
    public void sendMidiByte(int port, int value) {
        PdBase.sendMidiByte(port, value);
    }

    @PhonkMethod(description = "Gets an array from PdLib", example = "")
    @PhonkMethodParam(params = {"name", "size"})
    public float[] getArray(String source, int n) {
        // public void getArray(float[] destination, int destOffset, String
        // source, int srcOffset, int n) {
        // PdBase.readArray(destination, destOffset, source, srcOffset, n);

        float[] destination = new float[n];
        PdBase.readArray(destination, 0, source, 0, n);

        return destination;
    }

    @PhonkMethod(description = "Sends and array of floats to PdLib", example = "")
    @PhonkMethodParam(params = {"name", "array", "size"})
    public void sendArray(String destination, float[] source, int n) {
        PdBase.writeArray(destination, 0, source, 0, n);
    }

    private void initSystemServices() {
        TelephonyManager telephonyManager =
                (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                if (state == TelephonyManager.CALL_STATE_IDLE) startAudio();
                else stopAudio();
            }
        }, PhoneStateListener.LISTEN_CALL_STATE);
    }

    public void __stop() {
        stop();
    }
}