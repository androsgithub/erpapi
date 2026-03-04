package com.api.erp.v1.main.features.address.domain.entity;

import com.api.erp.v1.main.features.permission.domain.entity.BasePermissions;

/**
 * Address-specific permission constants.
 * <p>
 * Defines all permissions related to Address management.
 * Uses BasePermissions for standard operations.
 */
public final class AddressPermissions {

    private AddressPermissions() {
    }

    public static final String PREFIX = "address";
    
    // Standard CRUD operations
    public static final String CREATE = PREFIX + "." + BasePermissions.OPERATION_CREATE;
    public static final String UPDATE = PREFIX + "." + BasePermissions.OPERATION_UPDATE;
    public static final String VIEW = PREFIX + "." + BasePermissions.OPERATION_VIEW;
    public static final String DELETE = PREFIX + "." + BasePermissions.OPERATION_DELETE;
}
