package com.api.erp.v1.main.migration.domain;

import com.api.erp.v1.main.master.permission.domain.entity.BasePermissions;

/**
 * Migration-specific permission constants.
 * <p>
 * Defines all permissions related to Migration management.
 * Uses BasePermissions for standard operations.
 */
public class MigrationPermissions {

    public static final String PREFIX = "migration";
    
    // View operation
    public static final String VIEW = PREFIX + "." + BasePermissions.OPERATION_VIEW;
}
