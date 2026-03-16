package com.api.erp.v1.main.master.permission.infrastructure.factory;

import com.api.erp.v1.main.master.permission.domain.repository.PermissionRepository;
import com.api.erp.v1.main.master.permission.domain.service.IPermissionService;
import com.api.erp.v1.main.master.permission.infrastructure.cache.PermissionCacheManager;
import com.api.erp.v1.main.master.permission.infrastructure.service.PermissionService;
import com.api.erp.v1.main.master.tenant.domain.service.ITenantService;
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

        IPermissionService service = new PermissionService(permissionRepository);

        service = aplicarDecorators(service);

        log.info("[PERMISSION FACTORY] PermissionService built successfully");
        return service;
    }

    private IPermissionService aplicarDecorators(IPermissionService service) {
        return service;
    }
}

