package com.api.erp.v1.main.tenant.infrastructure.interceptors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * LEGADO - Não utilizado mais
 * 
 * Toda a lógica foi consolidada em TenantValidationInterceptor.
 * Este arquivo está sendo mantido apenas para compatibilidade.
 * 
 * @deprecated Use {@link com.api.erp.v1.main.shared.infrastructure.security.interceptors.TenantValidationInterceptor}
 */
@Slf4j
@Component
@Deprecated(since = "1.0", forRemoval = true)
public class TenantContextInterceptor implements HandlerInterceptor {
    // Legacy file - remove in future version
}
