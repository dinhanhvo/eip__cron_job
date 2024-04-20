package com.eip.data.repository;

import com.eip.data.entity.CanMqttMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IMqttPublishModelRepository extends JpaRepository<CanMqttMessage, Long> {
}
