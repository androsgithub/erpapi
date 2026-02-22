package com.api.erp.v1.main.tenant.application.dto;

public record PermissionConfigRequest(
        boolean permissionValidationEnabled,
        boolean permissionCacheEnabled,
        boolean permissionAuditEnabled
) {
}
