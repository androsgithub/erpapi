package com.api.erp.v1.main.dynamic.features.product.domain.entity;

import com.api.erp.v1.main.master.permission.domain.entity.BasePermissions;

/**
 * Product-specific permission constants.
 * <p>
 * Defines all permissions related to Product management.
 * Uses BasePermissions for standard operations (CREATE, UPDATE, VIEW, DELETE, etc).
 */
public final class ProductPermissions {

    private ProductPermissions() {
    }

    public static final String PREFIX = "product";
    
    // Standard CRUD operations
    public static final String CREATE = PREFIX + "." + BasePermissions.OPERATION_CREATE;
    public static final String UPDATE = PREFIX + "." + BasePermissions.OPERATION_UPDATE;
    public static final String VIEW = PREFIX + "." + BasePermissions.OPERATION_VIEW;
    public static final String DELETE = PREFIX + "." + BasePermissions.OPERATION_DELETE;
    
    // Product-specific operations
    public static final String ACTIVATE = PREFIX + "." + BasePermissions.OPERATION_ACTIVATE;
    public static final String DEACTIVATE = PREFIX + "." + BasePermissions.OPERATION_DEACTIVATE;
    public static final String LOCK = PREFIX + "." + BasePermissions.OPERATION_LOCK;
    public static final String DISCONTINUE = PREFIX + "." + BasePermissions.OPERATION_DISCONTINUE;
}
