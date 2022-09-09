/*
 * Part of Phonk http://www.phonk.io
 * A prototyping platform for Android devices
 *
 * Copyright (C) 2013 - 2017 Victor Diaz Barrales @victordiaz (Protocoder)
 * Copyright (C) 2017 - Victor Diaz Barrales @victordiaz (Phonk)
 *
 * Phonk is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Phonk is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Phonk. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package io.phonk.server;

import android.content.Context;
import android.content.Intent;

import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import io.phonk.runner.apprunner.api.PDevice;
import io.phonk.runner.base.utils.MLog;

public class PhonkWebsocketServer extends WebSocketServer {
    private final String TAG = PhonkWebsocketServer.class.getSimpleName();
    private Context mContext;

    private int mPort;
    private int mNumConnections = 0;
    private final List<WebSocket> connections = new ArrayList<WebSocket>();
    private HashMap<String, WebSocketListener> listeners = new HashMap<String, WebSocketListener>();
    private ConnectionCallback mConnectionCallback;

    public interface WebSocketListener {
        void onUpdated(JSONObject jsonObject);
    }

    public PhonkWebsocketServer(Context c, int port) throws UnknownHostException {
        super(new InetSocketAddress(port));
        mPort = port;
        mContext = c;
        setReuseAddr(true);
    }

    public PhonkWebsocketServer(InetSocketAddress address, Draft d) {
        super(address, Collections.singletonList(d));
    }

    public void start() {
        super.start();
        // MLog.d(TAG, "Start websocket server at on port " + mPort);
    }

    public void stop() {
        try {
            super.stop();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        mNumConnections++;
        MLog.d(TAG, "New websocket connection " + mNumConnections);
        connections.add(conn);
        mConnectionCallback.connect(conn.getRemoteSocketAddress().getAddress().toString().substring(1));
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        MLog.d(TAG, "closed");
        connections.remove(conn);
        mConnectionCallback.disconnect(conn.getRemoteSocketAddress().getAddress().toString().substring(1));
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        // MLog.d(TAG, "Error:");
        ex.printStackTrace();

        if (conn != null) {
            mConnectionCallback.disconnect(conn.getRemoteSocketAddress().getAddress().toString().substring(1));
        }
    }

    @Override
    public void onStart() {
        // MLog.d(TAG, "Start websocket server at on port " + mPort);
    }

    public void send(String json) {
        for (WebSocket sock : connections) {
            if (sock.isOpen()) {
                sock.send(json);
            }
        }
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        MLog.d(TAG, "Received message --> " + message);

        JSONObject response = new JSONObject();
        try {
            JSONObject msg = new JSONObject(message);
            String module = (String) msg.get("module");
            switch (module) {
                case "dashboard":
                    sendDashboardReceiver(msg);
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            MLog.e(TAG, "Error in handleMessage" + e.toString());
            try {
                response = response.put("error", e.toString());
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        conn.send(response.toString());
    }

    private void sendDashboardReceiver(JSONObject msg) {
        Intent i = new Intent("io.phonk.intent.WEBEDITOR_RECEIVER");

        i.putExtra("data", msg.toString());
        mContext.sendBroadcast(i);
    }

    public void addListener(String name, WebSocketListener l) {
        listeners.put(name, l);
    }

    public void removeAllListeners() {
        listeners.clear();
    }


    public void addConnectionCallback(ConnectionCallback connectionCallback) {
        mConnectionCallback = connectionCallback;
    }

    public interface ConnectionCallback {
        void connect(String ip);

        void disconnect(String ip);
    }
}
