package com.api.erp.v1.features.permissao.infrastructure.decorator;

import com.api.erp.v1.features.permissao.domain.service.PermissaoService;
import com.api.erp.v1.features.permissao.infrastructure.cache.PermissaoCacheManager;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class PermissaoCacheServiceDecorator implements PermissaoService {

    private final PermissaoService delegate;
    private final PermissaoCacheManager cacheManager;

    @Override
    public boolean hasPermission(Long usuarioId, String permissaoCodigo, Map<String, String> contexto) {
        String cacheKey = String.format("%s:%s:%d", usuarioId, permissaoCodigo, contexto.hashCode());
        
        Optional<Boolean> cachedResult = cacheManager.getResultado(cacheKey);
        if (cachedResult.isPresent()) {
            return cachedResult.get();
        }

        boolean result = delegate.hasPermission(usuarioId, permissaoCodigo, contexto);
        cacheManager.putResultado(cacheKey, result);
        return result;
    }
}
