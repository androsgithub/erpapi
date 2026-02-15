package com.api.erp.v1.main.features.permissao.infrastructure.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
public class PermissaoCacheManager {

    // Cache para armazenar o resultado booleano de hasPermission
    // Chave: "usuarioId:permissaoCodigo:contextoHash"
    private final Cache<String, Boolean> resultCache;

    public PermissaoCacheManager() {
        this.resultCache = CacheBuilder.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(5000) // Aumentado para mais resultados
                .build();
    }

    public Optional<Boolean> getResultado(String key) {
        return Optional.ofNullable(resultCache.getIfPresent(key));
    }

    public void putResultado(String key, boolean result) {
        resultCache.put(key, result);
    }

    public void evictResultado(String key) {
        resultCache.invalidate(key);
    }

    public void evictAll() {
        resultCache.invalidateAll();
    }
}
