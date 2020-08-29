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

package io.phonk.runner.apprunner.api.network;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Map;

import io.phonk.runner.apidoc.annotation.PhonkClass;
import io.phonk.runner.apidoc.annotation.PhonkMethod;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.api.ProtoBase;
import io.phonk.runner.apprunner.api.common.ReturnInterface;
import io.phonk.runner.apprunner.api.common.ReturnObject;
import io.phonk.runner.base.utils.MLog;

@PhonkClass
public class PMqtt extends ProtoBase {
    private final String TAG = PMqtt.class.getSimpleName();

    private MqttAsyncClient client;
    private ReturnInterface mCallbackData;
    private ReturnInterface mCallbackConnected;
    private ReturnInterface mCallbackDisconnected;
    private ReturnInterface mCallbackStatus;
    private ReturnInterface mCallbackDataDelivered;
    private ReturnInterface mCallbackError;

    public PMqtt(AppRunner appRunner) {
        super(appRunner);
        appRunner.whatIsRunning.add(this);
    }

    /**
     * Connect to a broker. I needs an object as follows
     * {
     *     broker: 'tcp://192.168.1.10',
     *     clientId: 'phonk',
     *     user: 'myuser',
     *     password: 'mypassword'
     * }
     * @param connectionSettings
     * @return
     */
    @PhonkMethod
    public PMqtt connect(Map connectionSettings) {
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            client = new MqttAsyncClient((String) connectionSettings.get("broker"), (String) connectionSettings.get("clientId"), persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();

            if (connectionSettings.containsKey("user") && connectionSettings.containsKey("password")) {
                connOpts.setUserName((String) connectionSettings.get("user"));
                connOpts.setPassword(((String)connectionSettings.get("password")).toCharArray());
            }

            if (connectionSettings.containsKey("autoReconnect")) {
                connOpts.setAutomaticReconnect(((boolean) connectionSettings.get("autoReconnect")));
            }

            connOpts.setCleanSession(true);
            client.connect(connOpts, "", new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    ReturnObject ret = new ReturnObject();
                    ret.put("status", "connected");

                    mHandler.post(() -> {
                        if (mCallbackConnected != null) mCallbackConnected.event(ret);
                        if (mCallbackStatus != null) mCallbackStatus.event(ret);
                    });
                    MLog.d(TAG, "connectComplete");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    ReturnObject ret = new ReturnObject();
                    ret.put("status", "error");

                    mHandler.post(() -> {
                        if (mCallbackError != null) mCallbackError.event(ret);
                        if (mCallbackStatus != null) mCallbackStatus.event(ret);
                    });
                    MLog.d(TAG, "connectComplete");
                }
            });

            client.setCallback(new MqttCallbackExtended() {
                @Override
                public void connectComplete(boolean reconnect, String serverURI) {
                }

                @Override
                public void connectionLost(Throwable cause) {
                    MLog.d(TAG, "disconnected");
                    ReturnObject ret = new ReturnObject();
                    ret.put("status", "disconnected");
                    mHandler.post(() -> {
                        if (mCallbackDisconnected != null) mCallbackDisconnected.event(ret);
                        if (mCallbackStatus != null) mCallbackStatus.event(ret);
                    });
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    MLog.d(TAG, "messageArrived");
                    ReturnObject ret = new ReturnObject();
                    ret.put("status", "messageArrived");
                    ret.put("data", new String(message.getPayload()));
                    ret.put("isDuplicate", message.isDuplicate());
                    ret.put("isRetained", message.isRetained());
                    ret.put("qos", message.getQos());
                    ret.put("id", message.getId());
                    ret.put("topic", topic);
                    mHandler.post(() -> {
                        if (mCallbackData != null) mCallbackData.event(ret);
                    });
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    MLog.d(TAG, "messageDelivered");
                    ReturnObject ret = new ReturnObject();
                    ret.put("status", "messageDelivered");
                    mHandler.post(() -> {
                        if (mCallbackDataDelivered != null) mCallbackDataDelivered.event(ret);
                    });
                }
            });
        } catch(MqttException me) {
            System.out.println("reason "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
            me.printStackTrace();
        }

        return this;
    }


    /**
     * Subscribes to a certain topic. The data will arrive in the onNewData callback
     *
     * @param topic
     * @return
     */
    @PhonkMethod
    public PMqtt subscribe(final String topic, int qos) {
        try {
            client.subscribe(topic, qos);
        } catch (MqttException e) {
            e.printStackTrace();
        }

        return this;
    }

    public PMqtt subscribe(String topic) {
        return subscribe(topic, 2);
    }

    /**
     * Unsubscribe from a topic
     *
     * @param topic
     * @return
     */
    @PhonkMethod
    public PMqtt unsubscribe(final String topic) {
        try {
            client.unsubscribe(topic);
        } catch (MqttException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Callback that returns data from a subscribed topic
     * @param callback
     * @return
     */
    public PMqtt onNewData(ReturnInterface callback) {
        mCallbackData = callback;
        return this;
    }

    /**
     * Callback that returns connection status
     * @param callback
     * @return
     */
    public PMqtt onDataDelivery(ReturnInterface callback) {
        mCallbackDataDelivered = callback;
        return this;
    }

    /**
     * Callback that returns connection status
     * @param callback
     * @return
     */
    public PMqtt onConnected(ReturnInterface callback) {
        getAppRunner().pUi.toast("Please update to the new API onConnect, this will be deprecated in a future release, thx :)", true);
        onConnect(callback);
        return this;
    }

    /**
     * Callback that returns connection status
     * @param callback
     * @return
     */
    public PMqtt onDisconnected(ReturnInterface callback) {
        mCallbackDisconnected = callback;
        getAppRunner().pUi.toast("Please update to the new API onDisconnect, this will be deprecated in a future release, thx :)", true);

        onConnect(callback);
        return this;
    }

    /**
     * Callback that returns connection status
     * @param callback
     * @return
     */
    public PMqtt onConnect(ReturnInterface callback) {
        mCallbackConnected = callback;

        return this;
    }

    /**
     * Callback that returns connection status
     * @param callback
     * @return
     */
    public PMqtt onDisconnect(ReturnInterface callback) {
        mCallbackDisconnected = callback;
        return this;
    }

    /**
     * Callback that returns connection status
     * @param callback
     * @return
     */
    public PMqtt onError(ReturnInterface callback) {
        mCallbackError = callback;
        return this;
    }

    /**
     * Callback that returns connection status
     * @param callback
     * @return
     */
    public PMqtt onStatus(ReturnInterface callback) {
        mCallbackStatus = callback;
        return this;
    }

    /**
     * Publish to a given topic
     *
     * @param topic
     * @param data
     * @param qos
     * @param retain
     * @return
     */
    public PMqtt publish(final String topic, final String data, int qos, boolean retain) {
        MqttMessage message = new MqttMessage(data.getBytes());
        message.setQos(qos);
        // message.setRetained(retain);
        try {
            client.publish(topic, message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Disconnect from the broker
     *
     * @return
     */
    public PMqtt disconnect() {
        try {
            client.disconnect();
            client.close();
        } catch (MqttException e) {
            e.printStackTrace();
        }
        return this;
    }


    @Override
    public void __stop() {
        disconnect();
    }

}
