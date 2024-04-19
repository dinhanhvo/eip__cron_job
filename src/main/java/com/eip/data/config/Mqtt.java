package com.eip.data.config;


import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.mqttv5.client.IMqttAsyncClient;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttAsyncClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.common.MqttException;

@Slf4j
public class Mqtt {

    private static final String MQTT_PUB_ID = "cron-client-pub";
    private static final String MQTT_CLOUD_SERVER_ADDRES= "tcp://broker.emqx.io:1883";

    private static final String MQTT_SUB_ID = "cron-client-sub";
    private static final String MQTT_LOCAL_SERVER_ADDRES= "tcp://192.168.1.14:1883";
    private static IMqttAsyncClient instanceIntenal;

    private static IMqttAsyncClient instance;

    public static IMqttAsyncClient getInstanceIntenal() {


//            String topic        = "MQTT Examples";
//            String content      = "Message from MqttPublishSample";
//            int qos             = 2;
//            String broker       = "tcp://iot.eclipse.org:1883";
//            String clientId     = "JavaSample";
            
            MemoryPersistence persistence = new MemoryPersistence();

            try {
                if (instanceIntenal == null) {
                    instanceIntenal = new MqttAsyncClient(MQTT_LOCAL_SERVER_ADDRES, MQTT_SUB_ID, persistence);

                    MqttConnectionOptions connOpts = new MqttConnectionOptions();
                    connOpts.setCleanStart(false);

                    log.info("Connecting to broker: " + MQTT_LOCAL_SERVER_ADDRES);
                    IMqttToken token = instanceIntenal.connect(connOpts);
                    token.waitForCompletion();
                    log.info("Connected");

//                log.info("Publishing message: "+content);
//                MqttMessage message = new MqttMessage(content.getBytes());
//                message.setQos(qos);
//                token = instanceIntenal.publish(topic, message);
//                token.waitForCompletion();

//                log.info("Disconnected");
//                log.info("Close client.");
//                sampleClient.close();
//                System.exit(0);
                }
            } catch(MqttException me) {
                log.info("reason "+me.getReasonCode());
                log.info("msg "+me.getMessage());
                log.info("loc "+me.getLocalizedMessage());
                log.info("cause "+me.getCause());
                log.info("excep "+me);
                log.error(me.getMessage());
            }

        return instanceIntenal;
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

    private Mqtt() {

    }
}
