package com.api.erp.v1.main.dynamic.features.customfield.domain.entity;

import com.api.erp.v1.main.master.permission.domain.entity.BasePermissions;

/**
 * Custom Field-specific permission constants.
 * <p>
 * Defines all permissions related to Custom Field management.
 * Uses BasePermissions for standard operations.
 */
public final class CustomFieldPermissions {

    private CustomFieldPermissions() {
    }

    public static final String PREFIX = "custom-field";
    
    // Standard CRUD operations
    public static final String CREATE = PREFIX + "." + BasePermissions.OPERATION_CREATE;
    public static final String UPDATE = PREFIX + "." + BasePermissions.OPERATION_UPDATE;
    public static final String VIEW = PREFIX + "." + BasePermissions.OPERATION_VIEW;
    public static final String DELETE = PREFIX + "." + BasePermissions.OPERATION_DELETE;
}
