package com.api.erp.v1.features.empresa.application.dto;

public record ClienteConfigRequest(
        boolean clienteValidationEnabled,
        boolean clienteAuditEnabled,
        boolean clienteCacheEnabled,
        boolean clienteNotificationEnabled,
        boolean clienteTenantCustomizationEnabled
) {
}
