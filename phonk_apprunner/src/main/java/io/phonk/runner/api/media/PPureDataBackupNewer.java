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

package io.phonk.runner.api.media;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.service.PdService;
import org.puredata.android.utils.PdUiDispatcher;
import org.puredata.core.PdBase;
import org.puredata.core.PdListener;
import org.puredata.core.utils.PdDispatcher;

import java.io.IOException;

import io.phonk.runner.api.ProtoBase;
import io.phonk.runner.apidoc.annotation.ProtoMethod;
import io.phonk.runner.apidoc.annotation.ProtoMethodParam;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.base.utils.MLog;

public class PPureDataBackupNewer extends ProtoBase {

    private String TAG = PPureDataBackupNewer.class.getSimpleName();

    private final Object lock = new Object(); /* synchronize on this lock whenever you access pdService */
    PdService pdService = null;
    boolean play = true;

    private PdPatchCallback mCallbackfn;
    private PdUiDispatcher receiver;

    public String path;

    public PPureDataBackupNewer(AppRunner appRunner) {
        super(appRunner);
    }

    // initPdPatch callback
    public interface PdPatchCallback {
        void event(PDReturn o);
    }

    // return object to JS
    class PDReturn {
        public String type;
        public String source;
        public Object value;
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        /* This gets called when our service is bound and sets up */
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            synchronized(lock) {
                pdService = ((PdService.PdBinder)service).getService();
                initPd();   /* see below */
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    /* actually bind the service, which triggers the code above;
       this is the method you should call to launch Pd */
    public void initPdService() {
        // a separate thread is not strictly necessary, but it improves responsiveness */
        new Thread() {
            @Override
            public void run() {
                getContext().bindService(new Intent(getContext(), PdService.class),
                        serviceConnection, Context.BIND_AUTO_CREATE);
            }
        }.start();
    }

    /* this is how we initialize Pd */
    private void initPd() {


        /* here is where we bind the print statement catcher defined below */
        PdBase.setReceiver(myDispatcher);

        /* here we are adding the listener for various messages
         from Pd sent to "GUI", i.e., anything that goes into the object
         [s GUI] will send to the listener defined below */
        myDispatcher.addListener("GUI", myListener);
        startAudio();  /* see below */
    }


    /* this is where we'll save the handle of the Pd patch */
    int patch = 0;

    private void startAudio() {
        synchronized (lock) {
            if (pdService == null) return;
            if (!initAudio(2, 2) && !initAudio(1, 2)) {  /* see below */
                if (!initAudio(0, 2)) {
                    MLog.e(TAG, "Unable to initialize audio interface");
                    return;
                } else {
                    MLog.w(TAG, "No audio input available");
                }
            }
            if (patch == 0) {
                try {
                    /* assuming here that the patch zipfile contained a single
                       folder "patch/" that contains an _main.pd */
                    /* open Pd patch and save its handle for future reference */
                    patch = PdBase.openPatch(path);
                } catch (IOException e) {
                    MLog.e(TAG, e.toString());
                    // finish();
                    return;
                }
                try {
                    /* sleep for one second to give Pd a chance to load samples and such;
                       this is not always necessary, but not doing this may give rise to
                       obscure glitches when the patch contains audio files */
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // do nothing
                }
            }

            getAppRunner().whatIsRunning.add(this);
            pdService.startAudio();
        }
    }

    /* helper method for startAudio();
       try to initialize Pd audio for the given number of input/output channels,
       return true on success */
    private boolean initAudio(int nIn, int nOut) {
        try {
            pdService.initAudio(AudioParameters.suggestSampleRate(), nIn, nOut, -1); // negative values default to PdService preferences
        } catch (IOException e) {
            MLog.e(TAG, e.toString());
            return false;
        }
        return true;
    }

    private void stopAudio() {
        synchronized (lock) {
            if (pdService == null) return;
            /* consider ramping down the volume here to avoid clicks */
            pdService.stopAudio();
        }
    }

    public void stop() {
        MLog.d(TAG, "stopping Pdaudio");
        synchronized(lock) {
            /* make sure to release all resources */
            stopAudio();
            if (patch != 0) {
                PdBase.closePatch(patch);
                patch = 0;
            }
            myDispatcher.release();
            PdBase.release();
            try {
                getContext().unbindService(serviceConnection);
            } catch (IllegalArgumentException e) {
                // already unbound
                pdService = null;
            }
        }
    }


    /* We'll use this to catch print statements from Pd
       when the user has a [print] object */
    private final PdDispatcher myDispatcher = new PdUiDispatcher() {
        @Override
        public void print(String s) {
            MLog.i(TAG, "Pd print " + "" + s);
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
    /*
    receiver = new PdUiDispatcher() {

        public void sendBack(final PDReturn o) {

            if (mCallbackfn != null) {
                //mHandler.post(new Runnable() {
                //    @Override
                //    public void run() {
                mCallbackfn.event(o);
                //    }
                //});
            }

        }

        @Override
        public void print(String s) {
            MLog.d(TAG, "pd >> " + s);

            final PDReturn o = new PDReturn();
            o.type = "print";
            o.value = s;
            sendBack(o);
        }

        @Override
        public void receiveBang(String source) {
            MLog.d(TAG, "bang");

            PDReturn o = new PDReturn();
            o.type = "bang";
            o.source = source;

            sendBack(o);
        }

        @Override
        public void receiveFloat(String source, float x) {
            MLog.d(TAG, "float: " + x);

            PDReturn o = new PDReturn();
            o.type = "float";
            o.source = source;
            o.value = x;

            sendBack(o);
        }

        @Override
        public void receiveList(String source, Object... args) {
            MLog.d(TAG, "list: " + Arrays.toString(args));

            JSONArray jsonArray = new JSONArray();
            for (Object arg : args) {
                jsonArray.put(arg);
            }

            PDReturn o = new PDReturn();
            o.type = "list";
            o.source = source;
            o.value = jsonArray;

            sendBack(o);
        }

        @Override
        public void receiveMessage(String source, String symbol, Object... args) {
            MLog.d(TAG, "message: " + Arrays.toString(args));

            JSONArray jsonArray = new JSONArray();
            for (Object arg : args) {
                jsonArray.put(arg);
            }

            PDReturn o = new PDReturn();
            o.type = "message";
            o.source = source;
            o.value = jsonArray;

            sendBack(o);
        }

        @Override
        public void receiveSymbol(String source, String symbol) {
            MLog.d(TAG, "symbol: " + symbol);

            PDReturn o = new PDReturn();
            o.type = "symbol";
            o.source = source;
            o.value = symbol;

            sendBack(o);
        }

    };
    */

    public PPureDataBackupNewer onNewData(final PdPatchCallback callbackfn) {
        mCallbackfn = callbackfn;

        return this;
    }

    public void listenTo(String m) {
        receiver.addListener(m, null);
    }

    public void removeListener(String m) {
        receiver.removeListener(m, null);
    }

    @ProtoMethod(description = "Sends a message to PdLib", example = "")
    @ProtoMethodParam(params = {"recv", "value"})
    public void sendMessage(String recv, String value) {
        PdBase.sendMessage(recv, value);
    }

    @ProtoMethod(description = "Sends a symbol to PdLib", example = "")
    @ProtoMethodParam(params = {"recv", "value"})
    public void sendSymbol(String recv, String value) {
        PdBase.sendSymbol(recv, value);
    }

    @ProtoMethod(description = "Sends a bang to PdLib", example = "")
    @ProtoMethodParam(params = {"name"})
    public void sendBang(String name) {
        PdBase.sendBang(name);
    }

    @ProtoMethod(description = "Sends a float number to PdLib", example = "")
    @ProtoMethodParam(params = {"name", "value"})
    public void sendFloat(String name, int value) {
        PdBase.sendFloat(name, value);
    }

    @ProtoMethod(description = "Sends a note to PdLib", example = "")
    @ProtoMethodParam(params = {"channel", "pitch, velocity"})
    public void sendNoteOn(int channel, int pitch, int velocity) {
        PdBase.sendNoteOn(channel, pitch, velocity);
    }

    @ProtoMethod(description = "Sends a midibyte to PdLib", example = "")
    @ProtoMethodParam(params = {"port", "value"})
    public void sendMidiByte(int port, int value) {
        PdBase.sendMidiByte(port, value);
    }

    @ProtoMethod(description = "Gets an array from PdLib", example = "")
    @ProtoMethodParam(params = {"name", "size"})
    public float[] getArray(String source, int n) {
        // public void getArray(float[] destination, int destOffset, String
        // source, int srcOffset, int n) {
        // PdBase.readArray(destination, destOffset, source, srcOffset, n);

        float[] destination = new float[n];
        PdBase.readArray(destination, 0, source, 0, n);

        return destination;
    }

    @ProtoMethod(description = "Sends and array of floats to PdLib", example = "")
    @ProtoMethodParam(params = {"name", "array", "size"})
    public void sendArray(String destination, float[] source, int n) {
        PdBase.writeArray(destination, 0, source, 0, n);
    }

    private void initSystemServices() {
        TelephonyManager telephonyManager =
                (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                synchronized (lock) {
                    if (pdService == null) return;
                    if (state == TelephonyManager.CALL_STATE_IDLE) {
                        if (play && !pdService.isRunning()) {
                            startAudio();
                        }
                    } else {
                        if (pdService.isRunning()) {
                            stopAudio();
                        }
                    }
                }
            }
        }, PhoneStateListener.LISTEN_CALL_STATE);
    }

    public void __stop() {
        stop();
    }
}