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

package io.phonk.runner.base.network;

import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.DatagramChannel;
import java.util.Vector;

import de.sciss.net.OSCMessage;
import de.sciss.net.OSCReceiver;
import de.sciss.net.OSCTransmitter;
import io.phonk.runner.apprunner.api.common.ReturnInterface;
import io.phonk.runner.apprunner.api.common.ReturnObject;
import io.phonk.runner.apprunner.interpreter.PhonkNativeArray;
import io.phonk.runner.base.utils.MLog;

public class OSC {

    protected static final String TAG = OSC.class.getSimpleName();

    public interface OSCServerListener {
        void onMessage(OSCMessage msg);
    }

    public static class Server {
        public final Handler mHandler = new Handler(Looper.getMainLooper());

        // OSC server
        OSCReceiver rcv;
        OSCTransmitter trns;
        DatagramChannel dch;

        SocketAddress inPort = null;
        final Vector<OSCServerListener> listeners = new Vector<>();

        public void start(String port) {
            rcv = null;
            dch = null;

            try {
                inPort = new InetSocketAddress(Integer.parseInt(port));

                dch = DatagramChannel.open();
                dch.socket().bind(inPort); // assigns an automatic local socket
                // address
                rcv = OSCReceiver.newUsing(dch);

                rcv.addOSCListener((msg, sender, time) -> {
                    for (OSCServerListener l : listeners) {
                        l.onMessage(msg);
                    }
                });
                rcv.startListening();

            } catch (IOException e2) {
                MLog.d(TAG, e2.getLocalizedMessage());
            }
        }

        public void onNewData(final ReturnInterface callbackfn) {
            this.addListener(msg -> {
                final PhonkNativeArray valuesArray = new PhonkNativeArray(0);
                for (int i = 0; i < msg.getArgCount(); i++) {
                    valuesArray.put(valuesArray.size(), valuesArray, msg.getArg(i));
                }

                mHandler.post(() -> {
                    // MLog.d(TAG, "receiver");
                    ReturnObject o = new ReturnObject();
                    o.put("name", msg.getName());
                    o.put("data", valuesArray);
                    callbackfn.event(o);
                });
            });
        }

        public void stop() {
            stopOSCServer();
        }

        public void __stop() {
            stop();
        }

        public void stopOSCServer() {
            try {
                dch.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            rcv.dispose();
        }

        public void addListener(OSCServerListener listener) {
            listeners.add(listener);
        }

        public void removeListener(OSCServerListener listener) {
            listeners.remove(listener);
        }
    }

    public static class Client {
        // OSC client
        SocketAddress addr2;
        DatagramChannel dch2;
        OSCTransmitter trns2;
        boolean oscConnected = false;

        public Client(String address, int port) {
            connectOSC(address, port);
        }

        public void connectOSC(String address, int port) {
            try {
                addr2 = new InetSocketAddress(InetAddress.getByName(address), port);
                dch2 = DatagramChannel.open();
                dch2.socket().bind(null);
                trns2 = OSCTransmitter.newUsing(dch2);
                oscConnected = true;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public boolean isOSCConnected() {
            return oscConnected;
        }

        public void send(final String msg, final Object[] o) {

            if (oscConnected) {
                Thread t = new Thread(() -> {
                    MLog.d(TAG, "sending");
                    try {
                        MLog.d(TAG, "sent");
                        trns2.send(new OSCMessage(msg, o), addr2);
                    } catch (IOException e) {
                        MLog.d(TAG, "not sent");
                        e.printStackTrace();
                    }

                });
                t.start();
            }
        }

        public void disconnectOSC() {
            try {
                dch2.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            trns2.dispose();
        }

        public void stop() {
            disconnectOSC();
        }

        public void __stop() {
            stop();
        }

    }

}
