package com.api.erp.v1.tenant.application.dto;

public record PermissaoConfigRequest(
        boolean permissaoValidationEnabled,
        boolean permissaoCacheEnabled,
        boolean permissaoAuditEnabled
) {
}
