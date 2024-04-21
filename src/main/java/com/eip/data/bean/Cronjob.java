package com.eip.data.bean;

import com.eip.data.config.Mqtt;
import com.eip.data.entity.MilkCollect;
import com.eip.data.service.MilkCollectService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.voda.eip.Converter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class Cronjob {

    @Autowired
    MilkCollectService milkCollectService;

    @Autowired
    BridgerService bridgerService;

    @Scheduled(fixedRate = 10000)
    @Transactional
    public void scheduleTaskWithFixedRate() {
        List<MilkCollect> milkCollects = milkCollectService.getMilkCollectsByStatus("Processing");
        log.info("-------- cronjob running ----- num uncompleted msg {}", milkCollects.size());
        if (milkCollects.isEmpty()) {
            return;
        }
        MilkCollect msg = milkCollects.get(0);
//        milkCollects.forEach(msg -> {
            try {
                String sTopic = "response/" +msg.getId()+ "/ThuMuaSua";
                Mqtt.controlSubscribe(Mqtt.getCloudInstance(), sTopic, bridgerService);
                log.info("----------- subscribed on {}", sTopic);

//                TimeUnit.MILLISECONDS.sleep(100);

                MqttMessage mqttMessage = new MqttMessage();
                String json = Converter.getObjectMapper().writeValueAsString(msg);
                mqttMessage.setPayload(json.getBytes(StandardCharsets.UTF_8));
                Mqtt.controlPublish(Mqtt.getCloudInstance(), "ThuMuaSua", mqttMessage);
                log.info("--------- published {} to cloud {}", msg.getId(), sTopic);
//                TimeUnit.MILLISECONDS.sleep(600);
            } catch (JsonProcessingException | MqttException e) {
                log.error(e.getMessage());
                throw new RuntimeException(e);
            }

//        });
    }

    public void scheduleTaskWithFixedDelay() {
    }

    public void scheduleTaskWithInitialDelay() {
    }

    public void scheduleTaskWithCronExpression() {
    }
}
