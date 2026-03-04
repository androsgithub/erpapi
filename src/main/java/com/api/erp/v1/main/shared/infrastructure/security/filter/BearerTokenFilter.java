package com.api.erp.v1.main.shared.infrastructure.security.filter;

import com.api.erp.v1.main.datasource.routing.TenantContext;
import com.api.erp.v1.main.shared.common.constant.HeaderConst;
import com.api.erp.v1.main.shared.domain.entity.UserAutenticado;
import com.api.erp.v1.main.shared.infrastructure.security.jwt.BearerTokenAuthentication;
import com.api.erp.v1.main.shared.infrastructure.security.jwt.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
                String userId = jwtTokenProvider.getUserIdFromToken(token);
                String tenantId = jwtTokenProvider.getTenantIdFromToken(token);

                log.debug("🔍 JWT Claims | email: {} | userId: {} | tenantId: {}", email, userId, tenantId);

                // Validation: se userId é null/vazio, não processa
                if (userId == null || userId.isEmpty()) {
                    log.warn("Valid JWT but userId is empty! Claims: email={}, tenantId={}", email, tenantId);
                    filterChain.doFilter(request, response);
                    return;
                }

                UserAutenticado user =
                        new UserAutenticado(userId, tenantId);

                BearerTokenAuthentication authentication =
                        new BearerTokenAuthentication(token, email, user);

                authentication.setDetails(userId);

                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);

                // ✅ CRITICAL: Set TenantContext for DataSource routing
                if (tenantId != null && !tenantId.isEmpty()) {
                    TenantContext.setTenantId(Long.valueOf(tenantId));
                    log.debug("✅ TenantContext setado do JWT | tenantId: {}", tenantId);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
