package com.eip.data.config;

import com.eip.data.Constant.Constant;
import com.eip.data.entity.MilkCollect;
import com.eip.data.repository.IMilkCollectRepository;
import com.eip.data.service.MilkCollectService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.voda.eip.Converter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;

@Slf4j
@Service
public class EipMqttCallback implements MqttCallback {

    @Autowired
    MilkCollectService milkCollectService;

    @Override
    public void disconnected(MqttDisconnectResponse mqttDisconnectResponse) {
        log.info("---------------------- disconnected ---------------");
    }

    @Override
    public void mqttErrorOccurred(MqttException e) {
        log.info("---------------------- mqttErrorOccurred ---------------");
    }

    @Override
    public synchronized void messageArrived(String topic, MqttMessage mqttMessage)  {

        Instant start = Instant.now();
        try {
            log.info("=====1============= received: {} on topic {}", mqttMessage, topic);

            if (topic.startsWith(Constant.TOPIC_RESPONSE_PRE)) {
                // the cloud received the msg => save COMPLETE to db
                MilkCollect milkCollect = Converter.objectMapper.readValue(new String(mqttMessage.getPayload()), MilkCollect.class);
                Long id = milkCollect.getId();
                log.info("====3============ Cloud has received the id ---{}---", id);

                MilkCollect milkCollectUpdate = milkCollectService.getMilkCollectById(id);
                log.info("====4============ search from db ---{}---", milkCollectUpdate);
                if (milkCollectUpdate != null) {
                    milkCollectService.updateStatusMilkCollectById(id, Constant.COMPLETED);
                    log.info("====5============ Saved ---{}---", milkCollectUpdate);
                    Mqtt.controlUnSubscribe(Mqtt.getCloudInstance(), topic);
                    log.info("====6============ Unsubscribe ---{}---", topic);
                } else {
                    log.info(" Can not find the message id: {}", id);
                }
            } else  {
                // if (topic.equalsIgnoreCase("ThuMuaSua"))
                // 1. subscribe the response
                // 2. publish to cloud
//                MilkCollect milkCollect = Converter.getObjectMapper().readValue(new String(mqttMessage.getPayload()), MilkCollect.class);

                // convert msg text to Obj
                MilkCollect milkCollect = Converter.messageToDTO(new String(mqttMessage.getPayload()));

                log.info("====4============== received: {} on topic {}", mqttMessage, topic);
                if (milkCollect == null) {
                    return ;
                }
                Long id = milkCollect.getId();
                if (id == null) {
                    return;
                }
                // response/id/ThuMuaSua
                String stopic = Constant.TOPIC_RESPONSE_PRE + Constant.SPLASH + id + Constant.SPLASH + topic;
                // listen the response
                Mqtt.controlSubscribe(Mqtt.getCloudInstance(), stopic);

                // object to json string
                String json = Converter.getObjectMapper().writeValueAsString(milkCollect);
                mqttMessage.setPayload(json.getBytes(StandardCharsets.UTF_8));

                Mqtt.controlPublish(Mqtt.getCloudInstance(), topic, mqttMessage);
                log.info("====5============== published: {}", mqttMessage);

            }

        } catch( MqttException me) {
            log.info("reason "+me.getReasonCode());
            log.info("msg "+me.getMessage());
            log.info("loc "+me.getLocalizedMessage());
            log.info("cause "+me.getCause());
            log.info("excep "+me);
            log.error(me.getMessage());

            Mqtt.restart();

        } catch (JsonMappingException | JsonParseException e) {
            log.info("====8============");
            log.error(e.getMessage());
            Mqtt.restart();
            throw new RuntimeException(e);
        } catch (IOException e) {
            log.info("====9============");
            log.error(e.getMessage());
            Mqtt.restart();
            throw new RuntimeException(e);
        } finally {
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            log.info("====timeElapsed=={}==========", timeElapsed);
        }
    }

    @Override
    public void deliveryComplete(IMqttToken iMqttToken) {
        log.info("---------------------- deliveryComplete ---------------");
    }

    @Override
    public void connectComplete(boolean b, String s) {
        log.info("---------------------- connectComplete ---------------");
    }

    @Override
    public void authPacketArrived(int i, MqttProperties mqttProperties) {
        log.info("---------------------- authPacketArrived ---------------");
    }

}
