package com.api.erp.v1.main.shared.infrastructure.service;

import com.api.erp.v1.main.shared.infrastructure.security.jwt.BearerTokenAuthentication;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityService {

    public String getAuthTenantId() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication instanceof BearerTokenAuthentication)) {
            return null;
        }

        BearerTokenAuthentication auth = (BearerTokenAuthentication) authentication;

        return auth.getPrincipal().getTenantId();
    }

    public String getAuthUserId() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication instanceof BearerTokenAuthentication)) {
            return null;
        }

        BearerTokenAuthentication auth =
                (BearerTokenAuthentication) authentication;

        return auth.getPrincipal().getUserId();
    }
}

