package com.api.erp.v1.shared.infrastructure.security.filters;

import com.api.erp.v1.shared.common.constant.HeaderConst;
import com.api.erp.v1.shared.domain.entity.UsuarioAutenticado;
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
                String tenantSlug = jwtTokenProvider.getTenantSlugFromToken(token);

                UsuarioAutenticado usuario =
                        new UsuarioAutenticado(usuarioId, tenantId, tenantSlug);

                BearerTokenAuthentication authentication =
                        new BearerTokenAuthentication(token, email, usuario);

                authentication.setDetails(usuarioId);

                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}

