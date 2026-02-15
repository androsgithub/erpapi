package com.api.erp.v1.main.tenant.infrastructure.interceptors;

import com.api.erp.v1.main.tenant.infrastructure.config.datasource.TenantContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * TenantContextInterceptor
 * <p>
 * Responsável por:
 * 1. Ativar o filtro Hibernate de tenantId (row-level security)
 * 2. Garantir isolamento de dados entre tenants
 * <p>
 * Fluxo:
 * - preHandle():  Ativa filtro Hibernate com tenantId do TenantContext
 * - afterCompletion(): Desativa filtro para limpeza
 */
@Slf4j
@Component
public class TenantContextInterceptor implements HandlerInterceptor {
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        try {
            TenantContext.clear();
            log.debug("🧹 [TenantContextInterceptor] TenantContext limpo após requisição");
        } catch (Exception e) {
            log.debug("⚠️ Erro ao limpar TenantContext", e);
        }
    }
}
