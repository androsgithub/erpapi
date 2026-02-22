package com.api.erp.v1.main.tenant.domain.entity.configs;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class PermissionConfig {

    @Column(name = "permission_validation_enabled", nullable = false)
    private Boolean permissionValidationEnabled = false;

    @Column(name = "permission_cache_enabled", nullable = false)
    private Boolean permissionCacheEnabled = false;

    @Column(name = "permission_audit_enabled", nullable = false)
    private Boolean permissionAuditEnabled = false;

    public boolean isPermissionValidationEnabled() {
        return Boolean.TRUE.equals(permissionValidationEnabled);
    }

    public boolean isPermissionCacheEnabled() {
        return Boolean.TRUE.equals(permissionCacheEnabled);
    }

    public boolean isPermissionAuditEnabled() {
        return Boolean.TRUE.equals(permissionAuditEnabled);
    }
}
