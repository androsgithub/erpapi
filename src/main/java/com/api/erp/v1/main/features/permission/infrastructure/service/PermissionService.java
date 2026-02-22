package com.api.erp.v1.main.features.permission.infrastructure.service;

import com.api.erp.v1.main.features.permission.domain.repository.PermissionRepository;
import com.api.erp.v1.main.features.permission.domain.service.IPermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
public class PermissionService implements IPermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository userPermissionRepository) {
        this.permissionRepository = userPermissionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasPermission(Long userId, String permissionCodigo, Map<String, String> contexto) {
        return permissionRepository.countPermission(userId, permissionCodigo) > 0;
    }

}
