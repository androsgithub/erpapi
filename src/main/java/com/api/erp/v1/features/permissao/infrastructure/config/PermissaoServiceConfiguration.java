package com.api.erp.v1.features.permissao.infrastructure.config;

import com.api.erp.v1.features.permissao.domain.repository.PermissaoRepository;
import com.api.erp.v1.features.permissao.domain.service.IPermissaoService;
import com.api.erp.v1.features.permissao.infrastructure.cache.PermissaoCacheManager;
import com.api.erp.v1.features.permissao.infrastructure.factory.PermissaoServiceFactory;
import com.api.erp.v1.features.permissao.infrastructure.factory.PermissaoServiceHolder;
import com.api.erp.v1.features.permissao.infrastructure.factory.PermissaoServiceProxy;
import com.api.erp.v1.tenant.domain.service.ITenantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Slf4j
@Configuration
public class PermissaoServiceConfiguration {

    @Bean
    public PermissaoServiceFactory permissaoServiceFactory(
            PermissaoRepository permissaoRepository,
            PermissaoCacheManager cacheManager,
            ITenantService tenantService) {
        log.info("[PERMISSAO CONFIG] Criando PermissaoServiceFactory");
        return new PermissaoServiceFactory(permissaoRepository, cacheManager, tenantService);
    }

    @Bean
    public PermissaoServiceHolder permissaoServiceHolder(
            PermissaoServiceFactory factory) {
        log.info("[PERMISSAO CONFIG] Criando PermissaoServiceHolder");
        PermissaoServiceHolder holder = new PermissaoServiceHolder();
        IPermissaoService serviceComDecorators = factory.create();
        holder.updateService(serviceComDecorators);
        log.info("[PERMISSAO CONFIG] PermissaoServiceHolder inicializado com sucesso");
        return holder;
    }

    @Bean
    @Primary
    public IPermissaoService permissaoServiceProxy(PermissaoServiceHolder holder) {
        log.info("[PERMISSAO CONFIG] Registrando PermissaoServiceProxy como bean principal");
        return new PermissaoServiceProxy(holder);
    }
}
