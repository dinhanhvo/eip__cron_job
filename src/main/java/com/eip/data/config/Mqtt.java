package com.eip.data.config;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

public class Mqtt {

    private static final String MQTT_PUB_ID = "cron-client-pub";
    private static final String MQTT_CLOUD_SERVER_ADDRES= "tcp://broker.emqx.io:1883";

    private static final String MQTT_SUB_ID = "cron-client-sub";
    private static final String MQTT_LOCAL_SERVER_ADDRES= "tcp://192.168.1.14:1883";
    private static IMqttClient instanceIntenal;

    private static IMqttClient instance;

    public static IMqttClient getInstanceIntenal() {
        try {
            if (instanceIntenal == null) {
                instanceIntenal = new MqttClient(MQTT_LOCAL_SERVER_ADDRES, MQTT_SUB_ID);
            }

            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);

            if (!instanceIntenal.isConnected()) {
                instanceIntenal.connect(options);
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }

        return instanceIntenal;
    }

    public static IMqttClient getInstance() {
        try {
            if (instance == null) {
                instance = new MqttClient(MQTT_CLOUD_SERVER_ADDRES, MQTT_PUB_ID);
            }

            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);

            if (!instance.isConnected()) {
                instance.connect(options);
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }

        return instance;
    }

    private Mqtt() {

    }
}
