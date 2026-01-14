package com.api.erp.v1.shared.infrastructure.security.resolver;

import com.api.erp.v1.shared.domain.enums.TenantAccessType;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.AntPathMatcher;

import java.util.List;
import java.util.Map;

public class EndpointSecurityResolver {

    private static final AntPathMatcher matcher = new AntPathMatcher();

    private static final Map<TenantAccessType, List<String>> ACCESS_RULES = Map.of(
            TenantAccessType.PUBLIC, List.of(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/swagger-resources/**",
                    "/webjars/**",
                    "/health"
            ),
            TenantAccessType.PUBLIC_WITH_TENANT, List.of(
                    "/api/v1/usuarios/login"
            ),
            TenantAccessType.AUTHENTICATED, List.of(

            ),
            TenantAccessType.AUTHENTICATED_WITH_TENANT, List.of(
                    "/api/v1/usuarios/me",
                    "/api/v1/pedidos/**",
                    "/api/v1/financeiro/**"
            )
    );


    public static TenantAccessType resolveByPath(HttpServletRequest request) {
        String uri = request.getRequestURI();

        // 1️⃣ Rotas públicas SEM tenant
        if (matches(TenantAccessType.PUBLIC, uri)) {
            return TenantAccessType.PUBLIC;
        }

        // 2️⃣ Rotas públicas COM tenant
        if (matches(TenantAccessType.PUBLIC_WITH_TENANT, uri)) {
            return TenantAccessType.PUBLIC_WITH_TENANT;
        }

        // 3️⃣ Autenticadas SEM tenant
        if (matches(TenantAccessType.AUTHENTICATED, uri)) {
            return TenantAccessType.AUTHENTICATED;
        }

        // 4️⃣ Autenticadas COM tenant
        if (matches(TenantAccessType.AUTHENTICATED_WITH_TENANT, uri)) {
            return TenantAccessType.AUTHENTICATED_WITH_TENANT;
        }

        // 🚨 fallback explícito
        return TenantAccessType.AUTHENTICATED_WITH_TENANT;
    }

    private static boolean matches(TenantAccessType type, String uri) {
        return ACCESS_RULES
                .getOrDefault(type, List.of())
                .stream()
                .anyMatch(pattern -> matcher.match(pattern, uri));
    }

}
