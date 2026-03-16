package com.api.erp.v1.main.master.tenant.application.dto;

public record PermissionConfigRequest(
        boolean permissionValidationEnabled,
        boolean permissionCacheEnabled,
        boolean permissionAuditEnabled
) {
}
