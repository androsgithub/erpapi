package com.api.erp.v1.main.features.product.domain.entity;

import com.api.erp.v1.main.features.permission.domain.entity.BasePermissions;

/**
 * Composition-specific permission constants.
 * <p>
 * Defines all permissions related to Product Composition management.
 * Uses BasePermissions for standard operations.
 */
public final class CompositionPermissions {

    private CompositionPermissions() {
    }

    public static final String PREFIX = "composition";
    
    // Standard CRUD operations
    public static final String CREATE = PREFIX + "." + BasePermissions.OPERATION_CREATE;
    public static final String UPDATE = PREFIX + "." + BasePermissions.OPERATION_UPDATE;
    public static final String VIEW = PREFIX + "." + BasePermissions.OPERATION_VIEW;
    public static final String DELETE = PREFIX + "." + BasePermissions.OPERATION_DELETE;
}
