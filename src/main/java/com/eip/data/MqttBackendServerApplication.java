package com.eip.data;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MqttBackendServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MqttBackendServerApplication.class, args);
    }

}
