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
 * Interceptor para validação de Tenant e Token JWT baseado no tipo de rota.
 *
 * Valida diferentes tipos de rotas com regras específicas:
 * 
 * 1. PUBLIC: Não valida nada (sem token, sem tenant)
 * 2. PUBLIC_WITH_TENANT: Valida apenas X-Tenant-ID (sem token)
 * 3. AUTHENTICATED: Valida apenas token JWT (sem tenant)
 * 4. AUTHENTICATED_WITH_TENANT: Valida token + X-Tenant-ID + correspondência entre eles
 *
 * Responsibilities:
 * - Resolver o tipo de acesso da rota (PUBLIC, PUBLIC_WITH_TENANT, AUTHENTICATED, AUTHENTICATED_WITH_TENANT)
 * - Validar X-Tenant-ID quando necessário
 * - Extrair e validar token JWT quando necessário
 * - Garantir correspondência entre X-Tenant-ID (header) e tenantId do token (quando ambos são exigidos)
 * - Popular o TenantContext com o tenantId quando apropriado
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

        // 1️⃣ ROTA PÚBLICA - não valida nada
        if (accessType == TenantAccessType.PUBLIC) {
            log.debug("[TenantValidation] ✓ Public route (no validation required) | route: {}", requestUri);
            return true;
        }

        String tenantIdHeader = request.getHeader(HeaderConst.TENANT_ID_HEADER);
        String token = jwtTokenProvider.extractToken(request);

        // 2️⃣ ROTA PÚBLICA COM TENANT - só valida X-Tenant-ID
        if (accessType == TenantAccessType.PUBLIC_WITH_TENANT) {
            return validatePublicWithTenant(request, response, tenantIdHeader, requestUri);
        }

        // 3️⃣ ROTA AUTENTICADA - só valida token
        if (accessType == TenantAccessType.AUTHENTICATED) {
            return validateAuthenticated(request, response, token, requestUri);
        }

        // 4️⃣ ROTA AUTENTICADA COM TENANT - valida token + X-Tenant-ID + correspondência
        if (accessType == TenantAccessType.AUTHENTICATED_WITH_TENANT) {
            return validateAuthenticatedWithTenant(request, response, token, tenantIdHeader, requestUri);
        }

        return false;
    }

    /**
     * Valida rotas públicas com tenant.
     * Apenas verifica se X-Tenant-ID está presente e é válido.
     */
    private boolean validatePublicWithTenant(
            HttpServletRequest request, HttpServletResponse response,
            String tenantIdHeader, String requestUri) {

        if (tenantIdHeader == null || tenantIdHeader.trim().isEmpty()) {
            log.warn("[TenantValidation] ✗ PUBLIC_WITH_TENANT: X-Tenant-ID is required | route: {}", requestUri);
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                    "X-Tenant-ID header is required");
            return false;
        }

        try {
            Long tenantIdFromHeader = Long.valueOf(tenantIdHeader);
            TenantContext.setTenantId(tenantIdFromHeader);
            log.debug("[TenantValidation] ✓ PUBLIC_WITH_TENANT: Tenant context set | tenantId: {} | route: {}",
                    tenantIdFromHeader, requestUri);
            return true;
        } catch (NumberFormatException e) {
            log.warn("[TenantValidation] ✗ PUBLIC_WITH_TENANT: Invalid X-Tenant-ID (not a number) " +
                    "| value: {} | route: {}", tenantIdHeader, requestUri);
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                    "X-Tenant-ID must be a valid number");
            return false;
        }
    }

    /**
     * Valida rotas autenticadas sem tenant.
     * Apenas verifica se o token é válido.
     */
    private boolean validateAuthenticated(
            HttpServletRequest request, HttpServletResponse response,
            String token, String requestUri) {

        if (token == null || token.isEmpty()) {
            log.warn("[TenantValidation] ✗ AUTHENTICATED: Token is required | route: {}", requestUri);
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "Authorization token is required");
            return false;
        }

        if (!jwtTokenProvider.isTokenValid(token)) {
            log.warn("[TenantValidation] ✗ AUTHENTICATED: Invalid or expired token | route: {}", requestUri);
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid or expired authorization token");
            return false;
        }

        log.debug("[TenantValidation] ✓ AUTHENTICATED: Valid token | route: {}", requestUri);
        return true;
    }

    /**
     * Valida rotas autenticadas com tenant.
     * Verifica se:
     * - O token é válido
     * - X-Tenant-ID está presente e é válido
     * - O tenantId do token corresponde ao X-Tenant-ID do header
     */
    private boolean validateAuthenticatedWithTenant(
            HttpServletRequest request, HttpServletResponse response,
            String token, String tenantIdHeader, String requestUri) {

        // Valida token
        if (token == null || token.isEmpty()) {
            log.warn("[TenantValidation] ✗ AUTHENTICATED_WITH_TENANT: Token is required | route: {}", requestUri);
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "Authorization token is required");
            return false;
        }

        if (!jwtTokenProvider.isTokenValid(token)) {
            log.warn("[TenantValidation] ✗ AUTHENTICATED_WITH_TENANT: Invalid or expired token | route: {}",
                    requestUri);
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid or expired authorization token");
            return false;
        }

        // Valida X-Tenant-ID
        if (tenantIdHeader == null || tenantIdHeader.trim().isEmpty()) {
            log.warn("[TenantValidation] ✗ AUTHENTICATED_WITH_TENANT: X-Tenant-ID is required | route: {}",
                    requestUri);
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                    "X-Tenant-ID header is required");
            return false;
        }

        try {
            Long tenantIdFromHeader = Long.valueOf(tenantIdHeader);

            // Valida se X-Tenant-ID do header corresponde ao tenantId do token
            if (!jwtTokenProvider.validateTenant(token, tenantIdFromHeader)) {
                log.warn("[TenantValidation] ✗ AUTHENTICATED_WITH_TENANT: " +
                        "X-Tenant-ID does not match JWT token | header: {} | route: {}",
                        tenantIdHeader, requestUri);
                sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN,
                        "X-Tenant-ID does not match JWT token");
                return false;
            }

            // Configura contexto
            TenantContext.setTenantId(tenantIdFromHeader);
            log.debug("[TenantValidation] ✓ AUTHENTICATED_WITH_TENANT: " +
                    "Valid token + valid tenant match | tenantId: {} | route: {}",
                    tenantIdFromHeader, requestUri);
            return true;

        } catch (NumberFormatException e) {
            log.warn("[TenantValidation] ✗ AUTHENTICATED_WITH_TENANT: " +
                    "Invalid X-Tenant-ID (not a number) | value: {} | route: {}",
                    tenantIdHeader, requestUri);
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                    "X-Tenant-ID must be a valid number");
            return false;
        }
    }

    /**
     * Utilitário para enviar respostas de erro em JSON.
     */
    private void sendErrorResponse(HttpServletResponse response, int status, String errorMessage) {
        try {
            response.setStatus(status);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"" + errorMessage + "\"}");
        } catch (Exception e) {
            log.error("[TenantValidation] Error writing error response", e);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
            Object handler, Exception ex) throws Exception {
        // Limpa o contexto após a requisição
        TenantContext.clear();
        log.debug("🧹 [TenantValidation] TenantContext limpo");
    }
}
