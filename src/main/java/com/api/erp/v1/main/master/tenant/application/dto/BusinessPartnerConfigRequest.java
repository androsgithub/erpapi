package com.api.erp.v1.main.master.tenant.application.dto;

public record BusinessPartnerConfigRequest(
        boolean businesspartnerValidationEnabled,
        boolean businesspartnerAuditEnabled,
        boolean businesspartnerCacheEnabled,
        boolean businesspartnerNotificationEnabled,
        boolean businesspartnerTenantCustomizationEnabled
) {
}
