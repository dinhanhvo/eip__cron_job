package com.eip.data.repository;

import com.eip.data.entity.MilkCollect;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IMilkCollectRepository extends JpaRepository<MilkCollect, Long> {

    List<MilkCollect> findByMqttStatus(String mqttStatus);



}
