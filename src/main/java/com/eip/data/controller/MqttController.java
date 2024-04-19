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
        Mqtt.getInstance().publish(messagePublishModel.getTopic(), mqttMessage);

    }

    @GetMapping("subscribe")
    public List<MqttSubscribeModel> subscribeChannel(@RequestParam(value = "topic") String topic,
                                                     @RequestParam(value = "wait_millis") Integer waitMillis)
            throws InterruptedException {
        List<MqttSubscribeModel> messages = new ArrayList<>();
        CountDownLatch countDownLatch = new CountDownLatch(10);

        IMqttAsyncClient mqttClient = Mqtt.getInstance();
//        mqttClient.subscribeWithResponse(topic, (s, mqttMessage) -> {
//            MqttSubscribeModel mqttSubscribeModel = new MqttSubscribeModel();
//            mqttSubscribeModel.setId(mqttMessage.getId());
//            mqttSubscribeModel.setMessage(new String(mqttMessage.getPayload()));
//            mqttSubscribeModel.setQos(mqttMessage.getQos());
//            messages.add(mqttSubscribeModel); // save to db
//            countDownLatch.countDown();
//
//        });

//        token.waitForCompletion();
        countDownLatch.await(waitMillis, TimeUnit.MILLISECONDS);

        return messages;
    }

    @GetMapping("eip/sub")
    public boolean subscribeEIP(@RequestParam(value = "topic") String topic) throws MqttException {

        IMqttAsyncClient mqttClient = Mqtt.getInstance();
        log.info("--------------- clientID: {}, subscribed on topic {}", mqttClient.getClientId(), topic);

        MqttProperties props = new MqttProperties();
        props.setSubscriptionIdentifiers(Arrays.asList(new Integer[] { 0 }));
        mqttClient.subscribe(new MqttSubscription(topic, 2), null, null, listenerService, props);

        return true;
    }

}
