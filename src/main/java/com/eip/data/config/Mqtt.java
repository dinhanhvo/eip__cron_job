package com.eip.data.config;


import com.eip.data.bean.BridgerService;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.mqttv5.client.IMqttAsyncClient;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttAsyncClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.MqttSubscription;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;

import java.util.Arrays;

@Slf4j
public class Mqtt {

    private static final String MQTT_PUB_ID = "cron-client-pub";
    private static final String MQTT_CLOUD_SERVER_ADDRES= "tcp://broker.emqx.io:1883";

    private static final String MQTT_SUB_ID = "cron-client-sub";
    private static final String MQTT_LOCAL_SERVER_ADDRES= "tcp://192.168.1.14:1883";
    private static IMqttAsyncClient instanceInternal;

    private static IMqttAsyncClient instance;

    public static IMqttAsyncClient getInstanceInternal() {

            MemoryPersistence persistence = new MemoryPersistence();

            try {
                if (instanceInternal == null) {
                    instanceInternal = new MqttAsyncClient(MQTT_LOCAL_SERVER_ADDRES, MQTT_SUB_ID, persistence);

                    MqttConnectionOptions connOpts = new MqttConnectionOptions();
                    connOpts.setCleanStart(false);

                    log.info("Connecting to broker: " + MQTT_LOCAL_SERVER_ADDRES);
                    IMqttToken token = instanceInternal.connect(connOpts);
                    token.waitForCompletion();
                    log.info("Connected");

                }
            } catch(MqttException me) {
                log.info("reason "+me.getReasonCode());
                log.info("msg "+me.getMessage());
                log.info("loc "+me.getLocalizedMessage());
                log.info("cause "+me.getCause());
                log.info("excep "+me);
                log.error(me.getMessage());
            }

        return instanceInternal;
    }

    public static IMqttAsyncClient getInstance() {

        MemoryPersistence persistence = new MemoryPersistence();

        try {
            if (instance == null) {
                instance = new MqttAsyncClient(MQTT_CLOUD_SERVER_ADDRES, MQTT_PUB_ID, persistence);

                MqttConnectionOptions connOpts = new MqttConnectionOptions();
                connOpts.setCleanStart(false);

                log.info("Connecting to broker: " + MQTT_CLOUD_SERVER_ADDRES);
                IMqttToken token = instance.connect(connOpts);
                token.waitForCompletion();
                log.info("Connected");

            }

        } catch(MqttException me) {
            log.info("reason "+me.getReasonCode());
            log.info("msg "+me.getMessage());
            log.info("loc "+me.getLocalizedMessage());
            log.info("cause "+me.getCause());
            log.info("MqttException: "+me);
        } catch (Exception e) {
            log.info("excep :"+e);
        }

        return instance;
    }

    public static void controlPublish(IMqttAsyncClient mqttClient, String topic, MqttMessage mqttMessage) {
        try {
            log.info("================== received: {}", mqttMessage);
            mqttClient.publish(topic, mqttMessage);
            log.info("================== published: {}", mqttMessage);
        }
        catch( MqttException me) {
            log.info("response failed reason "+me.getReasonCode());
            log.info("msg "+me.getMessage());
            log.info("loc "+me.getLocalizedMessage());
            log.info("cause "+me.getCause());
            log.info("excep "+me);
            log.error(me.getMessage());
        }
    }

    public static void controlSubscribe(IMqttAsyncClient mqttClient, String topic, BridgerService bridgerService) throws MqttException {
        MqttProperties props = new MqttProperties();
        props.setSubscriptionIdentifiers(Arrays.asList(new Integer[] { 0 }));
        mqttClient.subscribe(new MqttSubscription(topic, 2), null, null, bridgerService, props);

    }

    private Mqtt() {

    }
}
