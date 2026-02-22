package com.api.erp.v1.main.tenant.application.dto;

public record ContactConfigRequest(
        boolean contactValidationEnabled,
        boolean contactAuditEnabled,
        boolean contactCacheEnabled,
        boolean contactFormatValidationEnabled,
        boolean contactNotificationEnabled
) {
}
