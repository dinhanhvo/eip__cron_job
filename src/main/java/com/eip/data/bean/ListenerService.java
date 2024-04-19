package com.eip.data.bean;

import com.eip.data.config.Mqtt;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.mqttv5.client.IMqttAsyncClient;
import org.eclipse.paho.mqttv5.client.IMqttMessageListener;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ListenerService implements IMqttMessageListener {

//    @Autowired
//    IMqttPublishModelRepository mqttPublishModelRepository;

    @Bean
    public  void loadCloudClient() {
        IMqttAsyncClient mqttClient = Mqtt.getInstanceIntenal();
        log.info("--------------- clientID: {}", mqttClient.getClientId());

        IMqttAsyncClient mqttClientCloud = Mqtt.getInstance();
        log.info("--------------- mqttClientCloud: {}", mqttClientCloud.getClientId());


    }

//    @Override
//    public void messageArrived(String topic, MqttMessage mqttMessage) throws MqttException {
//
//        log.info("================== listen on: {}", topic);
////        CanMqttMessage canMqttMessage = new CanMqttMessage();
////        canMqttMessage.setMessage(new String(mqttMessage.getPayload()));
////        canMqttMessage.setQos(mqttMessage.getQos());
////        canMqttMessage.setTopic(topic);
////        mqttPublishModelRepository.save(canMqttMessage);
//        log.info("================== received: {}", mqttMessage);
//        Mqtt.getInstance().publish(topic, mqttMessage);
//        log.info("================== published: {}", mqttMessage);
//
//    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage)  {

        log.info("================== listen on: {}", topic);
//        CanMqttMessage canMqttMessage = new CanMqttMessage();
//        canMqttMessage.setMessage(new String(mqttMessage.getPayload()));
//        canMqttMessage.setQos(mqttMessage.getQos());
//        canMqttMessage.setTopic(topic);
//        mqttPublishModelRepository.save(canMqttMessage);

        try {
            log.info("================== received: {}", mqttMessage);
            Mqtt.getInstance().publish(topic, mqttMessage);
            log.info("================== published: {}", mqttMessage);
        } catch( MqttException me) {
            log.info("reason "+me.getReasonCode());
            log.info("msg "+me.getMessage());
            log.info("loc "+me.getLocalizedMessage());
            log.info("cause "+me.getCause());
            log.info("excep "+me);
            log.error(me.getMessage());
        }
    }
}
