package com.voda.eip;

import com.eip.data.model.MqttSubscribeModel;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;

@Slf4j
public  class MessageListener implements IMqttMessageListener {
    public int received = 0;

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) {
        log.info("received message, {}", mqttMessage);

        received++;
        MqttSubscribeModel mqttSubscribeModel = new MqttSubscribeModel();
        mqttSubscribeModel.setId(mqttMessage.getId());
        mqttSubscribeModel.setMessage(new String(mqttMessage.getPayload()));
        mqttSubscribeModel.setQos(mqttMessage.getQos());

    }
}
