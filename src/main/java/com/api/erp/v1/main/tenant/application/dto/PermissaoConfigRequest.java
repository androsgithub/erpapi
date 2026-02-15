package com.api.erp.v1.main.tenant.application.dto;

public record PermissaoConfigRequest(
        boolean permissaoValidationEnabled,
        boolean permissaoCacheEnabled,
        boolean permissaoAuditEnabled
) {
}
