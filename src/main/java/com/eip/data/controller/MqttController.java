package com.eip.data.controller;

import com.eip.data.bean.ListenerService;
import com.eip.data.config.Mqtt;
import com.eip.data.exceptions.ExceptionMessages;
import com.eip.data.exceptions.MqttException;
import com.eip.data.model.MqttPublishModel;
import com.eip.data.model.MqttSubscribeModel;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping(value = "/api/mqtt")
public class MqttController {

    @Autowired
    ListenerService listenerService;

    @PostMapping("publish")
    public void publishMessage(@RequestBody @Valid MqttPublishModel messagePublishModel,
                               BindingResult bindingResult) throws org.eclipse.paho.client.mqttv3.MqttException {
        if (bindingResult.hasErrors()) {
            throw new MqttException(ExceptionMessages.SOME_PARAMETERS_INVALID);
        }

        MqttMessage mqttMessage = new MqttMessage(messagePublishModel.getMessage().getBytes());
        mqttMessage.setQos(messagePublishModel.getQos());
        mqttMessage.setRetained(messagePublishModel.getRetained());

        Mqtt.getInstance().publish(messagePublishModel.getTopic(), mqttMessage);

    }

    @GetMapping("subscribe")
    public List<MqttSubscribeModel> subscribeChannel(@RequestParam(value = "topic") String topic,
                                                     @RequestParam(value = "wait_millis") Integer waitMillis)
            throws InterruptedException, org.eclipse.paho.client.mqttv3.MqttException {
        List<MqttSubscribeModel> messages = new ArrayList<>();
        CountDownLatch countDownLatch = new CountDownLatch(10);

        IMqttClient mqttClient = Mqtt.getInstance();
        mqttClient.subscribeWithResponse(topic, (s, mqttMessage) -> {
            MqttSubscribeModel mqttSubscribeModel = new MqttSubscribeModel();
            mqttSubscribeModel.setId(mqttMessage.getId());
            mqttSubscribeModel.setMessage(new String(mqttMessage.getPayload()));
            mqttSubscribeModel.setQos(mqttMessage.getQos());
            messages.add(mqttSubscribeModel); // save to db
            countDownLatch.countDown();

        });

//        token.waitForCompletion();
        countDownLatch.await(waitMillis, TimeUnit.MILLISECONDS);

        return messages;
    }

    @GetMapping("eip/sub")
    public List<MqttSubscribeModel> subscribeEIP(@RequestParam(value = "topic") String topic)
            throws org.eclipse.paho.client.mqttv3.MqttException {
        List<MqttSubscribeModel> messages = new ArrayList<>();

        IMqttClient mqttClient = Mqtt.getInstanceIntenal();
        log.info("--------------- clientID: {}, subcribed on topic {}", mqttClient.getClientId(), topic);
        IMqttToken token = mqttClient.subscribeWithResponse(topic, listenerService);

        token.waitForCompletion();

        return messages;
    }

}
