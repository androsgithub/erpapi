package com.api.erp.v1.shared.infrastructure.security.filter;

import com.api.erp.v1.features.tenant.domain.service.ITenantService;
import com.api.erp.v1.shared.common.constant.HeaderConst;
import com.api.erp.v1.shared.domain.entity.UsuarioAutenticado;
import com.api.erp.v1.shared.infrastructure.config.datasource.TenantContext;
import com.api.erp.v1.shared.infrastructure.security.jwt.BearerTokenAuthentication;
import com.api.erp.v1.shared.infrastructure.security.jwt.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class BearerTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader(HeaderConst.AUTHORIZATION_HEADER);

        if (authHeader != null && authHeader.startsWith(HeaderConst.BEARER_PREFIX)) {

            String token = authHeader.substring(HeaderConst.BEARER_PREFIX.length()).trim();

            if (jwtTokenProvider.isTokenValid(token)) {

                String email = jwtTokenProvider.getEmailFromToken(token);
                String usuarioId = jwtTokenProvider.getUsuarioIdFromToken(token);
                String tenantId = jwtTokenProvider.getTenantIdFromToken(token);

                log.debug("🔍 JWT Claims | email: {} | usuarioId: {} | tenantId: {}", email, usuarioId, tenantId);

                // Validação: se usuarioId é null/vazio, não processa
                if (usuarioId == null || usuarioId.isEmpty()) {
                    log.warn("⚠️ JWT válido mas usuarioId está vazio! Claims: email={}, tenantId={}", email, tenantId);
                    filterChain.doFilter(request, response);
                    return;
                }

                UsuarioAutenticado usuario =
                        new UsuarioAutenticado(usuarioId, tenantId);

                BearerTokenAuthentication authentication =
                        new BearerTokenAuthentication(token, email, usuario);

                authentication.setDetails(usuarioId);

                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);

                // ✅ CRITICAL: Set TenantContext for DataSource routing
                if (tenantId != null && !tenantId.isEmpty()) {
                    TenantContext.setTenantId(tenantId);
                    log.debug("✅ TenantContext setado do JWT | tenantId: {}", tenantId);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
