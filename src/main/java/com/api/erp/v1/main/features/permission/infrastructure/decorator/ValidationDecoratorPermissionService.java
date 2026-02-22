package com.api.erp.v1.main.features.permission.infrastructure.decorator;

import com.api.erp.v1.main.features.permission.domain.service.IPermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Validation decorator for the Permission service.
 * Responsible for validating parameters before processing permission logic.
 */
@Slf4j
@RequiredArgsConstructor
public class ValidationDecoratorPermissionService implements IPermissionService {

    private final IPermissionService delegate;

    @Override
    public boolean hasPermission(Long userId, String permissionCodigo, Map<String, String> contexto) {
        log.debug("[PERMISSION VALIDATION] Validating parameters - userId: {}, permissionCodigo: {}", 
                userId, permissionCodigo);

        validarParametros(userId, permissionCodigo);

        log.debug("[PERMISSION VALIDATION] Parameters validated successfully");
        return delegate.hasPermission(userId, permissionCodigo, contexto);
    }

    private void validarParametros(Long userId, String permissionCodigo) {
        if (userId == null || userId <= 0) {
            log.warn("[PERMISSION VALIDATION] Invalid userId: {}", userId);
            throw new IllegalArgumentException("userId must be greater than 0");
        }

        if (permissionCodigo == null || permissionCodigo.isBlank()) {
            log.warn("[PERMISSION VALIDATION] Invalid permissionCodigo: {}", permissionCodigo);
            throw new IllegalArgumentException("permissionCodigo cannot be null or empty");
        }
    }
}
