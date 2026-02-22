package com.api.erp.v1.main.tenant.application.dto;

public record CustomerConfigRequest(
        boolean customerValidationEnabled,
        boolean customerAuditEnabled,
        boolean customerCacheEnabled,
        boolean customerNotificationEnabled,
        boolean customerTenantCustomizationEnabled
) {
}
