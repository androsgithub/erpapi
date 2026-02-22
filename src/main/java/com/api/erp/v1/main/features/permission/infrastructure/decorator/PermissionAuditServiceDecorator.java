package com.api.erp.v1.main.features.permission.infrastructure.decorator;

import com.api.erp.v1.main.features.permission.domain.service.IPermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class PermissionAuditServiceDecorator implements IPermissionService {

    private final IPermissionService delegate;

    @Override
    public boolean hasPermission(Long userId, String permissionCodigo, Map<String, String> contexto) {
        log.info("Audit: Checking access for user {} on permission '{}' with context {}",
                userId, permissionCodigo, contexto);

        boolean result = delegate.hasPermission(userId, permissionCodigo, contexto);

        if (result) {
            log.info("Audit: Access GRANTED for user {} on permission '{}'", userId, permissionCodigo);
        } else {
            log.warn("Audit: Access DENIED for user {} on permission '{}'", userId, permissionCodigo);
        }
        return result;
    }
}
