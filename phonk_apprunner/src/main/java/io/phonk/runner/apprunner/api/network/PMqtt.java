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

import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.mqtt.client.Callback;
import org.fusesource.mqtt.client.CallbackConnection;
import org.fusesource.mqtt.client.Listener;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;

import io.phonk.runner.apidoc.annotation.PhonkClass;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.api.ProtoBase;
import io.phonk.runner.apprunner.api.common.ReturnInterface;
import io.phonk.runner.apprunner.api.common.ReturnObject;
import io.phonk.runner.base.utils.MLog;

@PhonkClass
public class PMqtt extends ProtoBase {
    private final String TAG = PMqtt.class.getSimpleName();

    private MQTT mMqtt;
    private CallbackConnection mConnection;
    private boolean mConnected = false;
    private ReturnInterface mCallback;
    HashMap<String, ReturnInterface> mSubscriptions = new HashMap<>();

    public PMqtt(AppRunner appRunner) {
        super(appRunner);

        appRunner.whatIsRunning.add(this);
    }

    public PMqtt connect(String clientId, String host, int port, String username, String password) {
        MLog.d(TAG, "connect 1");
        mMqtt = new MQTT();

        try {
            mMqtt.setHost(host, port);
            mMqtt.setClientId(clientId);
            mMqtt.setCleanSession(true);

            if (username != null && password != null) {
                mMqtt.setUserName(username);
                mMqtt.setPassword(password);
            }

            mConnection = mMqtt.callbackConnection();
            mConnection.listener(new Listener() {
                @Override
                public void onConnected() {
                    MLog.d(TAG, "mconnection onConnected");
                    ReturnObject ret = new ReturnObject();
                    ret.put("status", "connected");
                    mCallback.event(ret);
                }

                @Override
                public void onDisconnected() {
                    MLog.d(TAG, "mconnection onDisconnected");
                    ReturnObject ret = new ReturnObject();
                    ret.put("status", "disconnected");
                    mCallback.event(ret);
                }

                @Override
                public void onPublish(UTF8Buffer utf8Buffer, Buffer buffer, Runnable runnable) {
                    String topic = utf8Buffer.toString();
                    String data = buffer.toString();

                    MLog.d(TAG, "mconnection onPublish " + topic + " " + data);
                    // if(mCallback != null) mCallback.event(utf8Buffer.toString(), buffer.toString());
                    ReturnObject ret = new ReturnObject();
                    ret.put("status", "publish");
                    ret.put("topic", topic);
                    ret.put("data", data);
                    mCallback.event(ret);
                }

                @Override
                public void onFailure(Throwable throwable) {
                    MLog.d(TAG, "mconnection onFailure");
                    //callback.event(false);
                    ReturnObject ret = new ReturnObject();
                    ret.put("status", "connection_failure");
                    mCallback.event(ret);
                }
            });

            mConnection.connect(new Callback<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    mConnected = true;
                    MLog.d(TAG, "mconnection onSuccess");
                    // callback.event(mConnected);
                }

                @Override
                public void onFailure(Throwable throwable) {
                    mConnected = false;
                    MLog.d(TAG, "mconnection onFailure");
                    // callback.event(mConnected);
                }
            });

            MLog.d(TAG, "connect 2");


        } catch (URISyntaxException e) {
            e.printStackTrace();
            MLog.d(TAG, "connect :( 1");

        } catch (Exception e) {
            e.printStackTrace();
            MLog.d(TAG, "connect :( 2");
        }

        return this;
    }


    public PMqtt subscribe(final String topicStr) {

        Topic topic = new Topic(topicStr, QoS.AT_MOST_ONCE);
        Topic[] topics = new Topic[]{topic};

        mConnection.subscribe(topics, new Callback<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                String dataString = Arrays.toString(bytes);
                MLog.d(TAG, "subscribe onSuccess byte " + dataString);
                // callback.event(dataString);
                ReturnObject ret = new ReturnObject();
                ret.put("status", "subscribed");
                ret.put("topic", topicStr);
                mCallback.event(ret);
            }

            @Override
            public void onFailure(Throwable throwable) {
                MLog.d(TAG, "subscribe onFailure");
                ReturnObject ret = new ReturnObject();
                ret.put("status", "subscribe_fail");
                ret.put("topic", topicStr);
                mCallback.event(ret);
            }
        });

        return this;
    }

    public PMqtt unsubscribe(final String topic) {
        UTF8Buffer[] topics = new UTF8Buffer[1];
        topics[0] = new UTF8Buffer(topic);

        mConnection.unsubscribe(topics, new Callback<Void>() {
            @Override
            public void onSuccess(Void value) {
                ReturnObject ret = new ReturnObject();
                ret.put("status", "unsubscribed");
                ret.put("topic", topic);
                mCallback.event(ret);
            }

            @Override
            public void onFailure(Throwable value) {
                ReturnObject ret = new ReturnObject();
                ret.put("status", "unsubscribed_failed");
                ret.put("topic", topic);
                mCallback.event(ret);
            }
        });
        return this;
    }


    public PMqtt onNewData(ReturnInterface callback) {
        mCallback = callback;

        return this;
    }

    public PMqtt publish(final String topic, final String data) {
        boolean retain = false;

        mConnection.publish(topic, data.getBytes(), QoS.AT_MOST_ONCE, retain, new Callback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                MLog.d(TAG, "publish onSuccess");
                ReturnObject ret = new ReturnObject();
                ret.put("status", "published");
                ret.put("topic", topic);
                ret.put("data", data);
                mCallback.event(ret);
            }

            @Override
            public void onFailure(Throwable throwable) {
                MLog.d(TAG, "publish onFailure");
                ReturnObject ret = new ReturnObject();
                ret.put("status", "publish_fail");
                ret.put("topic", topic);
                ret.put("data", data);
                mCallback.event(ret);
            }
        });

        return this;
    }

    public PMqtt disconnect() {
        MLog.d(TAG, "disconnect");
        mConnection.disconnect(new Callback<Void>() {
            @Override
            public void onSuccess(Void value) {
                MLog.d(TAG, "disconnect onSuccess");
            }

            @Override
            public void onFailure(Throwable value) {
                MLog.d(TAG, "failure onFailure");
            }
        });

        return this;
    }


    @Override
    public void __stop() {
        disconnect();
    }

}
