package com.api.erp.v1.features.empresa.application.dto;

public record EnderecoConfigRequest(
        boolean enderecoValidationEnabled,
        boolean enderecoAuditEnabled,
        boolean enderecoCacheEnabled
) {
}
