package com.api.erp.v1.main.config;

import com.api.erp.v1.main.datasource.routing.TenantContext;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.stream.Collectors;

@Configuration
@EnableCaching
public class CachingConfig {
    @Bean("principalKeyGenerator")
    public KeyGenerator principalKeyGenerator() {
        return (target, method, params) -> {

            String tenantId = TenantContext.getTenantId().toString();

            String groups = TenantContext.getGroupIds()
                    .stream()
                    .sorted()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));

            String parameters = Arrays.stream(params)
                    .map(this::normalizeParam)
                    .collect(Collectors.joining(":"));

            return tenantId + ":" +
                    groups + ":" +
                    parameters;
        };
    }

    private String normalizeParam(Object param) {

        if (param instanceof Pageable pageable) {
            return pageable.getPageNumber() + "-" +
                    pageable.getPageSize() + "-" +
                    pageable.getSort().toString();
        }

        return String.valueOf(param);
    }
}
