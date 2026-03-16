package com.api.erp.v1.main.master.permission.infrastructure.config;

import com.api.erp.v1.main.master.permission.domain.repository.PermissionRepository;
import com.api.erp.v1.main.master.permission.domain.service.IPermissionService;
import com.api.erp.v1.main.master.permission.infrastructure.cache.PermissionCacheManager;
import com.api.erp.v1.main.master.permission.infrastructure.factory.PermissionServiceFactory;
import com.api.erp.v1.main.master.permission.infrastructure.factory.PermissionServiceHolder;
import com.api.erp.v1.main.master.permission.infrastructure.factory.PermissionServiceProxy;
import com.api.erp.v1.main.master.tenant.domain.service.ITenantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Slf4j
@Configuration
public class PermissionServiceConfiguration {

    @Bean
    public PermissionServiceFactory permissionServiceFactory(
            PermissionRepository permissionRepository,
            PermissionCacheManager cacheManager,
            ITenantService tenantService) {
        log.info("[PERMISSION CONFIG] Criando PermissionServiceFactory");
        return new PermissionServiceFactory(permissionRepository, cacheManager, tenantService);
    }

    @Bean
    public PermissionServiceHolder permissionServiceHolder(
            PermissionServiceFactory factory) {
        log.info("[PERMISSION CONFIG] Criando PermissionServiceHolder");
        PermissionServiceHolder holder = new PermissionServiceHolder();
        IPermissionService serviceComDecorators = factory.create();
        holder.updateService(serviceComDecorators);
        log.info("[PERMISSION CONFIG] PermissionServiceHolder inicializado com sucesso");
        return holder;
    }

    @Bean
    @Primary
    public IPermissionService permissionServiceProxy(PermissionServiceHolder holder) {
        log.info("[PERMISSION CONFIG] Logsndo PermissionServiceProxy como bean principal");
        return new PermissionServiceProxy(holder);
    }
}
