package com.eip.data.bean;

import com.eip.data.Constant.Constant;
import com.eip.data.config.Mqtt;
import com.eip.data.entity.MilkCollect;
import com.eip.data.service.MilkCollectService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.voda.eip.Converter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.mqttv5.client.IMqttAsyncClient;
import org.eclipse.paho.mqttv5.client.IMqttMessageListener;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.hibernate.annotations.Synchronize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class BridgerService implements IMqttMessageListener {

//    @Autowired
//    IMqttPublishModelRepository mqttPublishModelRepository;

    @Autowired
    MilkCollectService milkCollectService;

    @Bean
    public  void loadCloudClient() {
        IMqttAsyncClient mqttClient = Mqtt.getInstanceInternal();
        log.info("--------------- clientID: {}", mqttClient.getClientId());

        IMqttAsyncClient mqttClientCloud = Mqtt.getCloudInstance();
        log.info("--------------- mqttClientCloud: {}", mqttClientCloud.getClientId());

    }

    @Override
    public synchronized void messageArrived(String topic, MqttMessage mqttMessage)  {

        Instant start = Instant.now();
        try {
            log.info("=====1============= received: {} on topic {}", mqttMessage, topic);

            if (topic.startsWith(Constant.TOPIC_RESPONSE_PRE)) {
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
                    log.info(" Could not find the message id: {}", id);
                }
            } else  { // if (topic.equalsIgnoreCase("ThuMuaSua"))
//                MilkCollect milkCollect = Converter.getObjectMapper().readValue(new String(mqttMessage.getPayload()), MilkCollect.class);
                MilkCollect milkCollect = Converter.messageToDTO(new String(mqttMessage.getPayload()));

                log.info("====4============== received: {} on topic {}", mqttMessage, topic);
                if (milkCollect == null) {
                    return ;
                }
                Long id = milkCollect.getId();
                if (id == null) {
                    return;
                }
                String stopic = Constant.TOPIC_RESPONSE_PRE + id + Constant.SPLASH + topic;
                // listen the response
                Mqtt.controlSubscribe(Mqtt.getCloudInstance(), stopic, this);
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
}
