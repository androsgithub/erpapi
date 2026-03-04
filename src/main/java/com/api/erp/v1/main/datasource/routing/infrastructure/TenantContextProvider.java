package com.api.erp.v1.main.datasource.routing.infrastructure;

import com.api.erp.v1.main.datasource.routing.domain.ITenantContextProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * INFRASTRUCTURE - Implementação de ITenantContextProvider
 * 
 * Stores o contexto do tenant para cada thread/request.
 * Usa RequestContextHolder durante requests HTTP e ThreadLocal como fallback.
 * 
 * Thread-safe: cada thread tem seu próprio contexto
 * 
 * @author ERP System
 * @version 1.0
 */
@Component
@Slf4j
public class TenantContextProvider implements ITenantContextProvider {

    private static final ThreadLocal<String> TENANT_FALLBACK = new ThreadLocal<>();
    private static final String REQUEST_TENANT_ATTRIBUTE = "TENANT_ID";
    
    @Value("${erp.tenant.default:master}")
    private String defaultTenant;

    @Override
    public void setCurrentTenant(String tenantId) {
        if (tenantId == null || tenantId.trim().isEmpty()) {
            throw new IllegalArgumentException("Tenant ID cannot be null or empty");
        }
        
        tenantId = tenantId.trim();
        
        // Tenta armazenar em RequestContext se houver uma request ativa
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            attrs.setAttribute(REQUEST_TENANT_ATTRIBUTE, tenantId, ServletRequestAttributes.SCOPE_REQUEST);
            log.debug("Tenant context setado em RequestScope: {}", tenantId);
            return;
        } catch (IllegalStateException e) {
            log.debug("Sem request context, usando ThreadLocal para tenant: {}", tenantId);
        }
        
        // Fallback para ThreadLocal
        TENANT_FALLBACK.set(tenantId);
        log.debug("Tenant context setado em ThreadLocal: {}", tenantId);
    }

    @Override
    public String getCurrentTenant() {
        // 1. Tentar pegar de RequestContext primeiro
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            String tenant = (String) attrs.getAttribute(REQUEST_TENANT_ATTRIBUTE, ServletRequestAttributes.SCOPE_REQUEST);
            if (tenant != null) {
                log.debug("Tenant retrieved from RequestScope: {}", tenant);
                return tenant;
            }
        } catch (IllegalStateException e) {
            log.debug("No active request context");
        }
        
        // 2. Tentar pegar de ThreadLocal
        String tenant = TENANT_FALLBACK.get();
        if (tenant != null) {
            log.debug("Tenant retrieved from ThreadLocal: {}", tenant);
            return tenant;
        }
        
        // 3. Usar default tenant
        log.debug("No tenant defined, using default tenant: {}", defaultTenant);
        return defaultTenant;
    }

    @Override
    public void clearContext() {
        // Tenta limpar RequestContext
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            String tenant = (String) attrs.getAttribute(REQUEST_TENANT_ATTRIBUTE, ServletRequestAttributes.SCOPE_REQUEST);
            if (tenant != null) {
                log.debug("Cleaning tenant context from RequestScope: {}", tenant);
                attrs.removeAttribute(REQUEST_TENANT_ATTRIBUTE, ServletRequestAttributes.SCOPE_REQUEST);
            }
        } catch (IllegalStateException e) {
            log.debug("Sem request context, limpando ThreadLocal");
        }
        
        // Sempre limpa ThreadLocal como fallback
        String tenant = TENANT_FALLBACK.get();
        if (tenant != null) {
            log.debug("Cleaning tenant context from ThreadLocal: {}", tenant);
            TENANT_FALLBACK.remove();
        }
    }

    @Override
    public boolean hasTenant() {
        return getCurrentTenant() != null;
    }
}
