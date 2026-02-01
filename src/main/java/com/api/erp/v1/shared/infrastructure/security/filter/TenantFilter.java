package com.api.erp.v1.shared.infrastructure.security.filter;

import com.api.erp.v1.shared.common.constant.HeaderConst;
import com.api.erp.v1.tenant.infrastructure.config.datasource.TenantContext;
import jakarta.persistence.EntityManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro para extrair e armazenar informações de tenant da requisição
 */
@Slf4j
@Component
public class TenantFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String tenantId = request.getHeader(HeaderConst.TENANT_ID_HEADER);

        if (tenantId != null && !tenantId.isEmpty()) {
            TenantContext.setTenantId(tenantId);
            log.info("✅ TenantContext setado do HEADER | tenantId: {} | rota: {}", 
                    tenantId, request.getRequestURI());
        } else {
            log.debug("📍 Header X-Tenant-Id não encontrado | será obtido do JWT | rota: {}", 
                    request.getRequestURI());
        }

        // ⚠️ NÃO LIMPAR AQUI! TenantContextInterceptor fará a limpeza
        // Isso garante que o TenantContext esteja disponível para:
        // 1. TenantContextInterceptor.preHandle() ativar o filtro
        // 2. Controller executar com filtro ativo
        // 3. TenantContextInterceptor.afterCompletion() fazer limpeza
        filterChain.doFilter(request, response);
    }
}
