package com.api.erp.v1.tenant.application.dto;

import java.util.List;

public record UsuarioConfigRequest(
        boolean usuarioApprovalRequired,
        boolean usuarioCorporateEmailRequired,
        List<String> allowedEmailDomains
) {
}
