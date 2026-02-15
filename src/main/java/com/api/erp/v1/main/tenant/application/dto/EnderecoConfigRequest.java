package com.api.erp.v1.main.tenant.application.dto;

public record EnderecoConfigRequest(
        boolean enderecoValidationEnabled,
        boolean enderecoAuditEnabled,
        boolean enderecoCacheEnabled
) {
}
