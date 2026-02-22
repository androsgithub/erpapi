package com.api.erp.v1.main.config;

import com.api.erp.v1.main.features.permission.domain.service.IGerenciamentoPermissionService;
import com.api.erp.v1.main.features.permission.infrastructure.config.PermissionServiceConfiguration;
import com.api.erp.v1.main.features.permission.infrastructure.factory.GerenciamentoPermissionServiceFactory;
import com.api.erp.v1.main.features.measureunit.domain.service.IMeasureUnitService;
import com.api.erp.v1.main.features.measureunit.infrastructure.factory.MeasureUnitServiceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({PermissionServiceConfiguration.class})
public class ServiceConfiguration {

    @Bean
    public IMeasureUnitService measureUnitService(
            MeasureUnitServiceFactory factory) {
        return factory.create();
    }

    @Bean
    public IGerenciamentoPermissionService gerenciamentoPermissionService(
            GerenciamentoPermissionServiceFactory factory) {
        return factory.create();
    }
}
