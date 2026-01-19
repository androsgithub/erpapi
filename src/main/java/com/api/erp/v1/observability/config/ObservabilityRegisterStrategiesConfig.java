package com.api.erp.v1.observability.config;

import com.dros.observability.application.mapper.FlowErrorMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObservabilityConfig {
    @PostConstruct
    public void registerStrategies() {

//        FlowErrorMapper.registerCustomStrategy();
    }
}
