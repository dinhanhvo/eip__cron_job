package com.eip.data.service;

import com.eip.data.entity.MilkCollect;
import com.eip.data.repository.IMilkCollectRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class MilkCollectService {

    @Autowired
    IMilkCollectRepository milkCollectRepository;

    public List<MilkCollect> getMilkCollects() {
        return  milkCollectRepository.findAll();
    }
}
