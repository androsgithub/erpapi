package com.api.erp.v1.shared.infrastructure.service;

import com.api.erp.v1.shared.infrastructure.security.BearerTokenAuthentication;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityService {

    public Long getAuthTenentId() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        BearerTokenAuthentication auth =
                (BearerTokenAuthentication) authentication;

        return auth.getPrincipal().getTenantId();
    }

    public Long getAuthUsuarioId() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        BearerTokenAuthentication auth =
                (BearerTokenAuthentication) authentication;

        return auth.getPrincipal().getUsuarioId();
    }
}

