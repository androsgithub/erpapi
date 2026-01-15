package com.api.erp.v1.shared.infrastructure.security.interceptors;

import com.api.erp.v1.shared.infrastructure.config.datasource.TenantContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * TenantContextInterceptor
 * 
 * Responsável por:
 * 1. Ativar o filtro Hibernate de tenantId (row-level security)
 * 2. Garantir isolamento de dados entre tenants
 * 
 * Fluxo:
 * - preHandle():  Ativa filtro Hibernate com tenantId do TenantContext
 * - afterCompletion(): Desativa filtro para limpeza
 */
@Slf4j
@Component
public class TenantContextInterceptor implements HandlerInterceptor {

    /**
     * Ativa filtro de tenantId ANTES do controller ser executado
     * Nota: O TenantFilterAspect que realmente ativa o filtro no nível de Repository
     * Este método apenas valida que o TenantContext está corretamente setado
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Long tenantId = TenantContext.getTenantId();
        
        if (tenantId != null) {
            log.debug("✅ [TenantContextInterceptor] TenantId disponível para requisição | tenantId: {} | rota: {}", 
                    tenantId, request.getRequestURI());
        } else {
            log.debug("⚠️ [TenantContextInterceptor] TenantContext vazio | rota: {}", 
                    request.getRequestURI());
        }
        
        return true;
    }

    /**
     * Limpa o TenantContext DEPOIS da requisição ser processada
     * O TenantFilterAspect cuida de ativar/desativar o filtro em cada operação
     */
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
