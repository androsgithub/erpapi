package com.api.erp.v1.tenant.application.dto;

public record ContatoConfigRequest(
        boolean contatoValidationEnabled,
        boolean contatoAuditEnabled,
        boolean contatoCacheEnabled,
        boolean contatoFormatValidationEnabled,
        boolean contatoNotificationEnabled
) {
}
