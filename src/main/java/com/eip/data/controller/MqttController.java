package com.eip.data.controller;

import com.eip.data.bean.ListenerService;
import com.eip.data.config.Mqtt;
import com.eip.data.model.MqttPublishModel;
import com.eip.data.model.MqttSubscribeModel;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.mqttv5.client.IMqttAsyncClient;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.MqttSubscription;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping(value = "/api/mqtt")
public class MqttController {

    @Autowired
    ListenerService listenerService;

    @PostMapping("cloud/publish")
    public void cloudPublishMessage(@RequestBody @Valid MqttPublishModel messagePublishModel) throws MqttException {

        MqttMessage mqttMessage = new MqttMessage(messagePublishModel.getMessage().getBytes());
        mqttMessage.setQos(messagePublishModel.getQos());
        mqttMessage.setRetained(messagePublishModel.getRetained());

        Mqtt.getInstance().publish(messagePublishModel.getTopic(), mqttMessage);

    }

    @PostMapping("internal/publish")
    public void internalPublishMessage(@RequestBody @Valid MqttPublishModel messagePublishModel) throws MqttException {

        MqttMessage mqttMessage = new MqttMessage(messagePublishModel.getMessage().getBytes());
        mqttMessage.setQos(messagePublishModel.getQos());
        mqttMessage.setRetained(messagePublishModel.getRetained());
        Mqtt.getInstanceIntenal().publish(messagePublishModel.getTopic(), mqttMessage);

    }

    @GetMapping("internal/sub")
    public boolean subscribeEIP(@RequestParam(value = "topic") String topic) throws MqttException {

        IMqttAsyncClient mqttClient = Mqtt.getInstanceIntenal();
        log.info("--------------- clientID: {}, subscribed on topic {}", mqttClient.getClientId(), topic);

        MqttProperties props = new MqttProperties();
        props.setSubscriptionIdentifiers(Arrays.asList(new Integer[] { 0 }));
        mqttClient.subscribe(new MqttSubscription(topic, 2), null, null, listenerService, props);

        return true;
    }

    @GetMapping("cloud/sub")
    public boolean cloudSubscribeEIP(@RequestParam(value = "topic") String topic) throws MqttException {

        IMqttAsyncClient mqttClient = Mqtt.getInstance();
        log.info("--------------- clientID: {}, subscribed on topic {}", mqttClient.getClientId(), topic);

        MqttProperties props = new MqttProperties();
        props.setSubscriptionIdentifiers(Arrays.asList(new Integer[] { 0 }));
        mqttClient.subscribe(new MqttSubscription(topic, 2), null, null, listenerService, props);

        return true;
    }

}
