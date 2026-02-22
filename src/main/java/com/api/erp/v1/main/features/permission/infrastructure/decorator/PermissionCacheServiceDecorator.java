package com.api.erp.v1.main.features.permission.infrastructure.decorator;

import com.api.erp.v1.main.features.permission.domain.service.IPermissionService;
import com.api.erp.v1.main.features.permission.infrastructure.cache.PermissionCacheManager;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class PermissionCacheServiceDecorator implements IPermissionService {

    private final IPermissionService delegate;
    private final PermissionCacheManager cacheManager;

    @Override
    public boolean hasPermission(Long userId, String permissionCodigo, Map<String, String> contexto) {
        String cacheKey = String.format("%s:%s:%d", userId, permissionCodigo, contexto.hashCode());
        
        Optional<Boolean> cachedResult = cacheManager.getResultado(cacheKey);
        if (cachedResult.isPresent()) {
            return cachedResult.get();
        }

        boolean result = delegate.hasPermission(userId, permissionCodigo, contexto);
        cacheManager.putResultado(cacheKey, result);
        return result;
    }
}
