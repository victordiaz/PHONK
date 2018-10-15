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

package io.phonk.runner.api.network;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import io.phonk.runner.api.ProtoBase;
import io.phonk.runner.api.common.ReturnInterface;
import io.phonk.runner.api.common.ReturnObject;
import io.phonk.runner.apprunner.AppRunner;

import java.net.URI;
import java.net.URISyntaxException;

public class PWebSocketClient extends ProtoBase {

    private static final String TAG = "PWebSocketClient";
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private ReturnInterface mCallbackfn;
    private WebSocketClient mWebSocketClient = null;
    private boolean mIsConnected = false;

    public PWebSocketClient(AppRunner appRunner, String uri) {
        super(appRunner);

        Draft d = new Draft_17();

        try {
            mWebSocketClient = new WebSocketClient(new URI(uri), d) {

                @Override
                public void onOpen(ServerHandshake arg0) {
                    mIsConnected = true;
                    if (mCallbackfn == null) return;

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            ReturnObject o = new ReturnObject();
                            o.put("status", "open");
                            mCallbackfn.event(o);
                        }
                    });
                    //Log.d(TAG, "onOpen");
                }

                @Override
                public void onMessage(final String arg0) {
                    if (mCallbackfn == null) return;

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            ReturnObject o = new ReturnObject();
                            o.put("status", "message");
                            o.put("data", arg0);
                            mCallbackfn.event(o);
                        }
                    });

                    //Log.d(TAG, "onMessage client");

                }

                @Override
                public void onError(Exception arg0) {
                    if (mCallbackfn == null) return;

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            ReturnObject o = new ReturnObject();
                            o.put("status", "error");
                            mCallbackfn.event(o);
                        }
                    });

                    //Log.d(TAG, "onError");
                }

                @Override
                public void onClose(int arg0, String arg1, boolean arg2) {
                    if (mCallbackfn == null) return;

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            ReturnObject o = new ReturnObject();
                            o.put("status", "close");
                            mCallbackfn.event(o);
                        }
                    });

                    //Log.d(TAG, "onClose");

                }
            };
            mWebSocketClient.connect();

        } catch (URISyntaxException e) {
            Log.d(TAG, "error");

            ReturnObject o = new ReturnObject();
            o.put("status", "error");
            o.put("data", e.toString());

            mCallbackfn.event(o);
            e.printStackTrace();
        }
    }

    public PWebSocketClient onNewData(final ReturnInterface callbackfn) {
        mCallbackfn = callbackfn;

        return this;
    }

    public PWebSocketClient send(String msg) {
        if (mIsConnected) {
            mWebSocketClient.send(msg);
        }

        return this;
    }

    public boolean isConnected() {
        return mIsConnected;
    }

    @Override
    public void __stop() {
        mWebSocketClient.close();
    }
}
