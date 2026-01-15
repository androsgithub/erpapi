package com.api.erp.v1.shared.infrastructure.security.util;

import com.api.erp.v1.features.permissao.domain.service.IPermissaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PermissionEvaluator {

    private final IPermissaoService permissaoService;

    public boolean hasPermission(String permissaoCodigo) {
        return hasPermission(permissaoCodigo, Collections.emptyMap());
    }

    public boolean hasPermission(String permissaoCodigo, Map<String, String> contexto) {
        Long usuarioId = getUsuarioIdFromContext();
        return permissaoService.hasPermission(usuarioId, permissaoCodigo, contexto);
    }

    private Long getUsuarioIdFromContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getDetails() == null) {
            throw new IllegalStateException("Usuário não autenticado ou sem usuárioId no contexto");
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
            throw new IllegalStateException("Não foi possível converter usuarioId '" + detailsStr + "' para Long", e);
        }
    }
}
