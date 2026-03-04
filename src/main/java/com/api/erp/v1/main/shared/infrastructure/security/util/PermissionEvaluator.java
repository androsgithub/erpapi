package com.api.erp.v1.main.shared.infrastructure.security.util;

import com.api.erp.v1.main.features.permission.domain.service.IPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PermissionEvaluator {

    private final IPermissionService permissionService;

    public boolean hasPermission(String permissionCodigo) {
        return hasPermission(permissionCodigo, Collections.emptyMap());
    }

    public boolean hasPermission(String permissionCodigo, Map<String, String> contexto) {
        Long userId = getUserIdFromContext();
        return permissionService.hasPermission(userId, permissionCodigo, contexto);
    }

    private Long getUserIdFromContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getDetails() == null) {
            throw new IllegalStateException("User not authenticated or without userId in context");
        }

        Object details = authentication.getDetails();
        if (details == null) {
            throw new IllegalStateException("Authentication.getDetails() retornou null");
        }

        String detailsStr = details.toString();
        if (detailsStr == null || detailsStr.trim().isEmpty()) {
            throw new IllegalStateException("Authentication.getDetails().toString() retornou null ou vazio");
        }

        try {
            return Long.parseLong(detailsStr);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Failed to convert userId '" + detailsStr + "' to Long", e);
        }
    }
}
