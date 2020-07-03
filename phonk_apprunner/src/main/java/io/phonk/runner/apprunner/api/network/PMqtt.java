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
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Map;

import io.phonk.runner.apidoc.annotation.PhonkClass;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.api.ProtoBase;
import io.phonk.runner.apprunner.api.common.ReturnInterface;
import io.phonk.runner.apprunner.api.common.ReturnObject;
import io.phonk.runner.base.utils.MLog;

@PhonkClass
public class PMqtt extends ProtoBase {
    private final String TAG = PMqtt.class.getSimpleName();

    private MqttClient client;
    private ReturnInterface mCallback;

    public PMqtt(AppRunner appRunner) {
        super(appRunner);
        appRunner.whatIsRunning.add(this);
    }

    public PMqtt connect(Map connectionSettings) {
        MemoryPersistence persistence = new MemoryPersistence();

        MLog.d(TAG, "qq -> " + connectionSettings.get("broker"));

        try {
            client = new MqttClient((String) connectionSettings.get("broker"), (String) connectionSettings.get("clientId"), persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();

            if (connectionSettings.containsKey("user") && connectionSettings.containsKey("password")) {
                connOpts.setUserName((String) connectionSettings.get("user"));
                connOpts.setPassword(connectionSettings.get("password").toCharArray());
            }

            connOpts.setCleanSession(true);
            client.connect(connOpts);

            client.setCallback(new MqttCallbackExtended() {
                @Override
                public void connectComplete(boolean reconnect, String serverURI) {
                    ReturnObject ret = new ReturnObject();
                    ret.put("status", "connectComplete");
                    ret.put("broker", serverURI);

                    mHandler.post(() -> {
                        mCallback.event(ret);
                    });
                    MLog.d(TAG, "connectComplete");
                }

                @Override
                public void connectionLost(Throwable cause) {
                    MLog.d(TAG, "connectionLost");
                    ReturnObject ret = new ReturnObject();
                    ret.put("status", "connectionLost");
                    mHandler.post(() -> {
                        mCallback.event(ret);
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
                        mCallback.event(ret);
                    });
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    MLog.d(TAG, "deliveryComplete");
                    ReturnObject ret = new ReturnObject();
                    ret.put("status", "deliveryComplete");
                    mHandler.post(() -> {
                        mCallback.event(ret);
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


    public PMqtt subscribe(final String topic) {
        try {
            client.subscribe(topic);
        } catch (MqttException e) {
            e.printStackTrace();
        }

        return this;
    }

    public PMqtt unsubscribe(final String topic) {
        try {
            client.unsubscribe(topic);
        } catch (MqttException e) {
            e.printStackTrace();
        }
        return this;
    }


    public PMqtt onNewData(ReturnInterface callback) {
        mCallback = callback;
        return this;
    }

    public PMqtt publish(final String topic, final String data, int qos, boolean retain) {
        MqttMessage message = new MqttMessage(data.getBytes());
        message.setQos(qos);
        try {
            client.publish(topic, message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
        return this;
    }

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
