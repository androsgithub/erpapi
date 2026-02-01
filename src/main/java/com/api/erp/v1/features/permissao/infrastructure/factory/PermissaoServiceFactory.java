package com.api.erp.v1.features.permissao.infrastructure.factory;

import com.api.erp.v1.features.permissao.domain.repository.UsuarioPermissaoRepository;
import com.api.erp.v1.features.permissao.domain.service.IPermissaoService;
import com.api.erp.v1.features.permissao.infrastructure.cache.PermissaoCacheManager;
import com.api.erp.v1.features.permissao.infrastructure.decorator.PermissaoAuditServiceDecorator;
import com.api.erp.v1.features.permissao.infrastructure.decorator.PermissaoCacheServiceDecorator;
import com.api.erp.v1.features.permissao.infrastructure.decorator.ValidationDecoratorPermissaoService;
import com.api.erp.v1.features.permissao.infrastructure.service.PermissaoService;
import com.api.erp.v1.tenant.domain.entity.configs.PermissaoConfig;
import com.api.erp.v1.tenant.domain.service.ITenantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class PermissaoServiceFactory {

    private final UsuarioPermissaoRepository usuarioPermissaoRepository;
    private final PermissaoCacheManager cacheManager;
    private final ITenantService tenantService;

    public IPermissaoService create() {
        log.info("[PERMISSAO FACTORY] Construindo PermissaoService com decorators");
        
        PermissaoConfig config = obterConfiguracao();
        IPermissaoService service = new PermissaoService(usuarioPermissaoRepository);
        
        service = aplicarDecorators(service, config);
        
        log.info("[PERMISSAO FACTORY] PermissaoService construído com sucesso");
        return service;
    }

    private IPermissaoService aplicarDecorators(IPermissaoService service, PermissaoConfig config) {
        // Validação é aplicada primeiro
        if (config != null && config.isPermissaoValidationEnabled()) {
            service = new ValidationDecoratorPermissaoService(service);
            log.debug("[PERMISSAO FACTORY] ValidationDecorator aplicado");
        }

        // Cache é aplicado antes de auditoria
        if (config != null && config.isPermissaoCacheEnabled()) {
            service = new PermissaoCacheServiceDecorator(service, cacheManager);
            log.debug("[PERMISSAO FACTORY] CacheDecorator aplicado");
        }

        // Auditoria é a última
        if (config != null && config.isPermissaoAuditEnabled()) {
            service = new PermissaoAuditServiceDecorator(service);
            log.debug("[PERMISSAO FACTORY] AuditDecorator aplicado");
        }

        return service;
    }

    private PermissaoConfig obterConfiguracao() {
        try {
            return tenantService.getTenantConfig(null).getPermissaoConfig();
        } catch (Exception e) {
            log.warn("[PERMISSAO FACTORY] Não foi possível obter PermissaoConfig, " +
                    "usando apenas serviço base. Erro: {}", e.getMessage());
            return null;
        }
    }
}

