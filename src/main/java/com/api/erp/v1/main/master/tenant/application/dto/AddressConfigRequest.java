package com.api.erp.v1.main.master.tenant.application.dto;

public record AddressConfigRequest(
        boolean addressValidationEnabled,
        boolean addressAuditEnabled,
        boolean addressCacheEnabled
) {
}
