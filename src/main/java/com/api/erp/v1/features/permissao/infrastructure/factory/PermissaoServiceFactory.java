package com.api.erp.v1.features.permissao.infrastructure.factory;

import com.api.erp.v1.features.permissao.domain.service.PermissaoService;
import com.api.erp.v1.features.permissao.infrastructure.cache.PermissaoCacheManager;
import com.api.erp.v1.features.permissao.infrastructure.decorator.PermissaoAuditServiceDecorator;
import com.api.erp.v1.features.permissao.infrastructure.decorator.PermissaoCacheServiceDecorator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class PermissaoServiceFactory {

    @Bean
    @Primary
    public PermissaoService permissaoService(
            @Qualifier("permissaoServiceImpl") PermissaoService baseService,
            PermissaoCacheManager cacheManager) {
        
        // Monta a cadeia de decorators para o serviço de permissão
        // A ordem é importante: Audit -> Cache -> Implementação Base
        PermissaoService cachedService = new PermissaoCacheServiceDecorator(baseService, cacheManager);
        return new PermissaoAuditServiceDecorator(cachedService);
    }
}
