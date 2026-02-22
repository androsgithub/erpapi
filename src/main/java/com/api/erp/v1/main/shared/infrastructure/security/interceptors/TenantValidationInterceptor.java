package com.api.erp.v1.main.shared.infrastructure.security.interceptors;

import com.api.erp.v1.main.datasource.routing.TenantContext;
import com.api.erp.v1.main.shared.common.constant.HeaderConst;
import com.api.erp.v1.main.shared.infrastructure.security.jwt.JwtTokenProvider;
import com.api.erp.v1.main.shared.infrastructure.security.resolver.EndpointSecurityResolver;
import com.api.erp.v1.main.shared.domain.enums.TenantAccessType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Interceptor simples e enxuto para validação de Tenant
 *
 * Responsabilidades:
 * - Validar o header X-Tenant-ID para endpoints que o exigem
 * - Extrair e validar tenantId do token JWT
 * - Garantir que X-Tenant-ID (header) corresponde ao tenantId do token
 * - Popular o TenantContext para a requisição
 * - Limpar o contexto após o processamento
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class TenantValidationInterceptor implements HandlerInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String requestUri = request.getRequestURI();
        TenantAccessType accessType = EndpointSecurityResolver.resolveByPath(request);

        // If public, continue without validating tenant
        if (accessType == TenantAccessType.PUBLIC) {
            log.debug("[TenantValidation] Public URL: {}", requestUri);
            return true;
        }

        String tenantIdHeader = request.getHeader(HeaderConst.TENANT_ID_HEADER);

        // If requires tenant, validate header
        if (accessType == TenantAccessType.PUBLIC_WITH_TENANT ||
            accessType == TenantAccessType.AUTHENTICATED_WITH_TENANT) {

            if (tenantIdHeader == null || tenantIdHeader.trim().isEmpty()) {
                log.warn("[TenantValidation] Required X-Tenant-ID not provided | route: {}", requestUri);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"X-Tenant-ID header is required\"}");
                return false;
            }

            // Validates that it is a number
            try {
                Long tenantIdFromHeader = Long.valueOf(tenantIdHeader);

                // Extracts JWT token
                String token = jwtTokenProvider.extractToken(request);

                if (token != null && jwtTokenProvider.isTokenValid(token)) {
                    // Validates if X-Tenant-ID matches the tenantId from the token
                    if (!jwtTokenProvider.validateTenant(token, tenantIdFromHeader)) {
                        log.warn("[TenantValidation] X-Tenant-ID does not match token | " +
                                "header: {} | route: {}", tenantIdHeader, requestUri);
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"error\": \"X-Tenant-ID does not match JWT token\"}");
                        return false;
                    }
                }

                // Sets the context
                TenantContext.setTenantId(tenantIdFromHeader);
                log.debug("[TenantValidation] Tenant context set | tenantId: {} | route: {}",
                        tenantIdHeader, requestUri);

            } catch (NumberFormatException e) {
                log.warn("[TenantValidation] Invalid X-Tenant-ID (not a number) | value: {} | route: {}",
                        tenantIdHeader, requestUri);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"X-Tenant-ID must be a valid number\"}");
                return false;
            }
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
            Object handler, Exception ex) throws Exception {
        // Limpa o contexto após a requisição
        TenantContext.clear();
        log.debug("🧹 [TenantValidation] TenantContext limpo");
    }
}
