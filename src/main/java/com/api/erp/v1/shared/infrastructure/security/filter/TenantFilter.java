package com.api.erp.v1.shared.infrastructure.security.filters;

import com.api.erp.v1.shared.common.constant.HeaderConst;
import com.api.erp.v1.shared.domain.enums.TenantAccessType;
import com.api.erp.v1.shared.infrastructure.config.datasource.TenantContext;
import com.api.erp.v1.shared.infrastructure.security.jwt.JwtTokenProvider;
import com.api.erp.v1.shared.infrastructure.security.resolver.EndpointSecurityResolver;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class TenantFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            TenantAccessType reqType = EndpointSecurityResolver.resolveByPath(request);

            /* TENANT */
            if (reqType.requiresTenant()) {
                String tenantSlug = request.getHeader(HeaderConst.TENANT_SLUG_HEADER);
                String tenantId = request.getHeader(HeaderConst.TENANT_ID_HEADER);

                if (tenantSlug == null || tenantId == null) {
                    log.error("❌ Headers de tenant são obrigatórios! tenantSlug={}, tenantId={}", tenantSlug, tenantId);
                    writeError(response, HttpStatus.BAD_REQUEST, "Tenant headers are required (X-Tenant-Slug e X-Tenant-Id)");
                    return;
                }

                log.info("📍 TenantFilter: Setando contexto - slug='{}' | id='{}'", tenantSlug, tenantId);
                TenantContext.setTenantSlug(tenantSlug);
                TenantContext.setTenantId(tenantId);
            }

            /* AUTH */
            if (reqType.requiresAuth()) {
                String token = jwtTokenProvider.extractToken(request);

                if (token == null) {
                    writeError(response, HttpStatus.UNAUTHORIZED, "JWT token is required");
                    return;
                }

                if (reqType.requiresTenant()) {
                    if (!jwtTokenProvider.validateTenant(token,
                            TenantContext.getTenantSlug(),
                            TenantContext.getTenantId())) {

                        writeError(response, HttpStatus.FORBIDDEN, "Token does not belong to tenant");
                        return;
                    }
                }
            }

            filterChain.doFilter(request, response);

        } finally {
            TenantContext.clear();
        }
    }

    private void writeError(HttpServletResponse response, HttpStatus status, String message)
            throws IOException {

        response.setStatus(status.value());
        response.setContentType("application/json");
        response.getWriter().write("""
            { "error": "%s" }
        """.formatted(message));
    }
}

