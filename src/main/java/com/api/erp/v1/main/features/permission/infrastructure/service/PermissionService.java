package com.api.erp.v1.main.features.permission.infrastructure.service;

import com.api.erp.v1.main.features.permission.domain.repository.PermissionRepository;
import com.api.erp.v1.main.features.permission.domain.service.IPermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
public class PermissionService implements IPermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository userPermissionRepository) {
        this.permissionRepository = userPermissionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = "has-permission",
            keyGenerator = "principalKeyGenerator",
            unless = "#result == null"
    )
    public boolean hasPermission(Long userId, String permissionCodigo) {
        return permissionRepository.countPermission(userId, permissionCodigo) > 0;
    }

}
