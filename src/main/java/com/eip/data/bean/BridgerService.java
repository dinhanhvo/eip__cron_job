package com.eip.data.bean;

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

//        log.info("================== listen on: {}", topic);
//        CanMqttMessage canMqttMessage = new CanMqttMessage();
//        canMqttMessage.setMessage(new String(mqttMessage.getPayload()));
//        canMqttMessage.setQos(mqttMessage.getQos());
//        canMqttMessage.setTopic(topic);
//        mqttPublishModelRepository.save(canMqttMessage);

        Instant start = Instant.now();
        try {
            log.info("=====1============= received: {} on topic {}", mqttMessage, topic);

// CODE HERE
            if (topic.startsWith("response/")) {
                MilkCollect milkCollect = Converter.objectMapper.readValue(new String(mqttMessage.getPayload()), MilkCollect.class);
                Long id = milkCollect.getId();
                log.info("====3============ Cloud has received the id ---{}---", id);

                MilkCollect milkCollectUpdate = milkCollectService.getMilkCollectById(id);
                log.info("====4============ search from db ---{}---", milkCollectUpdate);
                if (milkCollectUpdate != null) {
                    milkCollectService.updateStatusMilkCollectById(id, "Completed");
                    log.info("====5============ Saved ---{}---", milkCollectUpdate);
                    Mqtt.controlUnSubscribe(Mqtt.getCloudInstance(), topic);
                    log.info("====6============ Unsubscribe ---{}---", topic);
                } else {
                    log.info(" Could not find the message id: {}", id);
                }
            } else  { // if (topic.equalsIgnoreCase("ThuMuaSua"))

//                MilkCollect milkCollect = Converter.getObjectMapper().readValue(new String(mqttMessage.getPayload()), MilkCollect.class);
                MilkCollect milkCollect = Converter.messageToDTO(new String(mqttMessage.getPayload()));

//                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("UTC"));
//                assert milkCollect != null;
//                milkCollect.setCreatedAt(milkCollect.getCreatedAt());

                log.info("====4============== received: {} on topic {}", mqttMessage, topic);
                if (milkCollect == null) {
                    return ;
                }
                Long id = milkCollect.getId();
                if (id == null) {
                    return;
                }
                String stopic = "response/" + id + "/" + topic;
                // listen the response
                Mqtt.controlSubscribe(Mqtt.getCloudInstance(), stopic, this);
//                log.info("====5============== subscribed on : {}", stopic);
//
//                log.info("====51============== msg id: {}", id);
                String json = Converter.getObjectMapper().writeValueAsString(milkCollect);
//                log.info("====52============== msg json: {}", json);
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
            Mqtt.restart();
            throw new RuntimeException(e);
        } catch (IOException e) {
            log.info("====9============");
            Mqtt.restart();
            throw new RuntimeException(e);
        } finally {
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            log.info("====timeElapsed=={}==========", timeElapsed);
        }
    }
}
