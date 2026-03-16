package com.api.erp.v1.main.master.tenant.application.dto;

public record ContactConfigRequest(
        boolean contactValidationEnabled,
        boolean contactAuditEnabled,
        boolean contactCacheEnabled,
        boolean contactFormatValidationEnabled,
        boolean contactNotificationEnabled
) {
}
