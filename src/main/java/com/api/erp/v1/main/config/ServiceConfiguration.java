package com.api.erp.v1.main.config;

import com.api.erp.v1.main.master.permission.domain.service.IManagementPermissionService;
import com.api.erp.v1.main.master.permission.infrastructure.config.PermissionServiceConfiguration;
import com.api.erp.v1.main.master.permission.infrastructure.factory.ManagementPermissionServiceFactory;
import com.api.erp.v1.main.dynamic.features.measureunit.domain.service.IMeasureUnitService;
import com.api.erp.v1.main.dynamic.features.measureunit.infrastructure.factory.MeasureUnitServiceFactory;
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
    public IManagementPermissionService gerenciamentoPermissionService(
            ManagementPermissionServiceFactory factory) {
        return factory.create();
    }
}
