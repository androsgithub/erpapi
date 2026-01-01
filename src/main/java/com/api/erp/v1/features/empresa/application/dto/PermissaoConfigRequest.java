package com.api.erp.v1.features.empresa.application.dto;

public record PermissaoConfigRequest(
        boolean permissaoValidationEnabled,
        boolean permissaoCacheEnabled,
        boolean permissaoAuditEnabled
) {
}
