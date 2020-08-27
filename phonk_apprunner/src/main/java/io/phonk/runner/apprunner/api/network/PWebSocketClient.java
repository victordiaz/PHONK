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

package io.phonk.runner.apprunner.api.network;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

import io.phonk.runner.apidoc.annotation.PhonkClass;
import io.phonk.runner.apidoc.annotation.PhonkMethod;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.api.ProtoBase;
import io.phonk.runner.apprunner.api.common.ReturnInterface;
import io.phonk.runner.apprunner.api.common.ReturnObject;

@PhonkClass
public class PWebSocketClient extends ProtoBase {
    private static final String TAG = PWebSocketClient.class.getSimpleName();

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private WebSocketClient mWebSocketClient = null;
    private boolean mIsConnected = false;
    private ReturnInterface mCallbackNewData;
    private ReturnInterface mCallbackConnect;
    private ReturnInterface mCallbackDisconnect;
    private ReturnInterface mCallbackError;
    private ReturnInterface mCallbackStatus;

    public PWebSocketClient(AppRunner appRunner, String uri) {
        super(appRunner);

        try {
            mWebSocketClient = new WebSocketClient(new URI(uri)) {

                @Override
                public void onOpen(ServerHandshake arg0) {
                    mIsConnected = true;

                    mHandler.post(() -> {
                        ReturnObject o = new ReturnObject();
                        o.put("status", "connected");
                        if (mCallbackConnect != null) mCallbackConnect.event(o);
                        if (mCallbackStatus != null) mCallbackStatus.event(o);
                    });
                    //Log.d(TAG, "onOpen");
                }

                @Override
                public void onMessage(final String arg0) {
                    mHandler.post(() -> {
                        ReturnObject o = new ReturnObject();
                        o.put("status", "message");
                        o.put("data", arg0);
                        if (mCallbackNewData != null) mCallbackNewData.event(o);
                    });

                    //Log.d(TAG, "onMessage client");
                }

                @Override
                public void onError(Exception arg0) {
                    mHandler.post(() -> {
                        ReturnObject o = new ReturnObject();
                        o.put("status", "error");
                        if (mCallbackError != null) mCallbackError.event(o);
                        if (mCallbackStatus != null) mCallbackStatus.event(o);
                    });

                    //Log.d(TAG, "onError");
                }

                @Override
                public void onClose(int arg0, String arg1, boolean arg2) {

                    mHandler.post(() -> {
                        ReturnObject o = new ReturnObject();
                        o.put("status", "disconnect");
                        if (mCallbackDisconnect != null) mCallbackDisconnect.event(o);
                        if (mCallbackStatus != null) mCallbackStatus.event(o);
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
            if (mCallbackError != null) mCallbackError.event(o);
            e.printStackTrace();
        }
    }

    /**
     * Callback that returns connection status
     * @param callback
     * @return
     */
    @PhonkMethod
    public PWebSocketClient onConnect(ReturnInterface callback) {
        mCallbackConnect = callback;
        return this;
    }

    /**
     * Callback that returns connection status
     * @param callback
     * @return
     */
    @PhonkMethod
    public PWebSocketClient onDisconnect(ReturnInterface callback) {
        mCallbackDisconnect = callback;
        return this;
    }

    /**
     * Callback that returns connection status
     * @param callback
     * @return
     */
    @PhonkMethod
    public PWebSocketClient onError(ReturnInterface callback) {
        mCallbackError = callback;
        return this;
    }

    /**
     * Callback that returns connection status
     * @param callback
     * @return
     */
    @PhonkMethod
    public PWebSocketClient onStatus(ReturnInterface callback) {
        mCallbackStatus = callback;
        return this;
    }

    /**
     * Callback that returns connection status
     * @param callback
     * @return
     */
    @PhonkMethod
    public PWebSocketClient onNewData(final ReturnInterface callback) {
        mCallbackNewData = callback;

        return this;
    }

    @PhonkMethod
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
