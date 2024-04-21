package com.eip.data.bean;

import com.eip.data.service.MilkCollectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class Cronjob {

    @Autowired
    MilkCollectService milkCollectService;

    @Scheduled(fixedRate = 10000)
    public void scheduleTaskWithFixedRate() {

    }

    public void scheduleTaskWithFixedDelay() {
    }

    public void scheduleTaskWithInitialDelay() {
    }

    public void scheduleTaskWithCronExpression() {
    }
}
