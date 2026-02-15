package com.api.erp.v1.external.observability.config;

import com.api.erp.v1.external.observability.strategy.BusinessErrorMappingStrategy;
import com.api.erp.v1.external.observability.strategy.ValidationErrorMappingStrategy;
import com.dros.observability.core.mapper.FlowErrorMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObservabilityRegisterStrategiesConfig {
    @PostConstruct
    public void registerStrategies() {
        FlowErrorMapper.registerCustomStrategy(new BusinessErrorMappingStrategy());
        FlowErrorMapper.registerCustomStrategy(new ValidationErrorMappingStrategy());
    }
}