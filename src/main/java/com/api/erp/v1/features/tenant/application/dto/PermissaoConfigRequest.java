package com.api.erp.v1.features.tenant.application.dto;

public record PermissaoConfigRequest(
        boolean permissaoValidationEnabled,
        boolean permissaoCacheEnabled,
        boolean permissaoAuditEnabled
) {
}
