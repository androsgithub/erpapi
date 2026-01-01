package com.api.erp.v1.features.empresa.application.dto;

public record ContatoConfigRequest(
        boolean contatoValidationEnabled,
        boolean contatoAuditEnabled,
        boolean contatoCacheEnabled,
        boolean contatoFormatValidationEnabled,
        boolean contatoNotificationEnabled
) {
}
