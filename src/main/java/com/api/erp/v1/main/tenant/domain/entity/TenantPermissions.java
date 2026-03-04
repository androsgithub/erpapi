package com.api.erp.v1.main.tenant.domain.entity;

import com.api.erp.v1.main.features.permission.domain.entity.BasePermissions;

/**
 * Tenant-specific permission constants.
 * <p>
 * Defines all permissions related to Tenant management.
 * Uses BasePermissions for standard operations.
 */
public final class TenantPermissions {

    private TenantPermissions() {
    }

    public static final String PREFIX = "tenant";
    
    // Standard operations
    public static final String SEARCH = PREFIX + "." + BasePermissions.OPERATION_SEARCH;
    public static final String UPDATE = PREFIX + "." + BasePermissions.OPERATION_UPDATE;
}


