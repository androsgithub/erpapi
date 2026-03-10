package com.api.erp.v1.main.features.permission.infrastructure.factory;

import com.api.erp.v1.main.features.permission.domain.repository.PermissionRepository;
import com.api.erp.v1.main.features.permission.domain.service.IPermissionService;
import com.api.erp.v1.main.features.permission.infrastructure.cache.PermissionCacheManager;
import com.api.erp.v1.main.features.permission.infrastructure.decorator.PermissionAuditServiceDecorator;
import com.api.erp.v1.main.features.permission.infrastructure.decorator.ValidationDecoratorPermissionService;
import com.api.erp.v1.main.features.permission.infrastructure.service.PermissionService;
import com.api.erp.v1.main.tenant.domain.entity.configs.PermissionConfig;
import com.api.erp.v1.main.tenant.domain.service.ITenantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class PermissionServiceFactory {

    private final PermissionRepository permissionRepository;
    private final PermissionCacheManager cacheManager;
    private final ITenantService tenantService;

    public IPermissionService create() {
        log.info("[PERMISSION FACTORY] Construindo PermissionService com decorators");

        PermissionConfig config = obterConfiguracao();
        IPermissionService service = new PermissionService(permissionRepository);

        service = aplicarDecorators(service, config);

        log.info("[PERMISSION FACTORY] PermissionService built successfully");
        return service;
    }

    private IPermissionService aplicarDecorators(IPermissionService service, PermissionConfig config) {
        // Validation é aplicada primeiro
        if (config != null && config.isPermissionValidationEnabled()) {
            service = new ValidationDecoratorPermissionService(service);
            log.debug("[PERMISSION FACTORY] ValidationDecorator aplicado");
        }

        // Auditoria é a última
        if (config != null && config.isPermissionAuditEnabled()) {
            service = new PermissionAuditServiceDecorator(service);
            log.debug("[PERMISSION FACTORY] AuditDecorator aplicado");
        }

        return service;
    }

    private PermissionConfig obterConfiguracao() {
        try {
            return tenantService.getTenantConfig(null).getPermissionConfig();
        } catch (Exception e) {
            log.warn("[PERMISSION FACTORY] Could not obtain PermissionConfig, "
 +
                    "usando apenas serviço base. Erro: {}", e.getMessage());
            return null;
        }
    }
}

