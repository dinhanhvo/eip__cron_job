package com.eip.data.bean;

import com.eip.data.config.Mqtt;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ListenerService implements IMqttMessageListener {

//    @Autowired
//    IMqttPublishModelRepository mqttPublishModelRepository;

    @Bean
    public  void loadCloudClient() throws MqttException {
        IMqttClient mqttClient = Mqtt.getInstanceIntenal();
        log.info("--------------- clientID: {}", mqttClient.getClientId());

        IMqttClient mqttClientCloud = Mqtt.getInstance();
        log.info("--------------- mqttClientCloud: {}", mqttClientCloud.getClientId());


    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws MqttException {

        log.info("================== listen on: {}", topic);
//        CanMqttMessage canMqttMessage = new CanMqttMessage();
//        canMqttMessage.setMessage(new String(mqttMessage.getPayload()));
//        canMqttMessage.setQos(mqttMessage.getQos());
//        canMqttMessage.setTopic(topic);
//        mqttPublishModelRepository.save(canMqttMessage);
        log.info("================== received: {}", mqttMessage);
        Mqtt.getInstance().publish(topic, mqttMessage);
        log.info("================== published: {}", mqttMessage);

    }
}
