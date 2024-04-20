package com.eip.data.util;

import com.eip.data.bean.BridgerService;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.mqttv5.client.IMqttAsyncClient;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.MqttSubscription;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;

import java.util.Arrays;

@Slf4j
public class MqttUtil {

    public  void controlPublish(IMqttAsyncClient mqttClient, String topic, MqttMessage mqttMessage) {
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

    public  void controlSubscribe(IMqttAsyncClient mqttClient, String topic, BridgerService bridgerService) throws MqttException {
        MqttProperties props = new MqttProperties();
        props.setSubscriptionIdentifiers(Arrays.asList(new Integer[] { 0 }));
        mqttClient.subscribe(new MqttSubscription(topic, 2), null, null, bridgerService, props);

    }

}
