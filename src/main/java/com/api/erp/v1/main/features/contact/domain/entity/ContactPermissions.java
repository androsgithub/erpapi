package com.api.erp.v1.main.features.contact.domain.entity;

import com.api.erp.v1.main.features.permission.domain.entity.BasePermissions;

/**
 * Contact-specific permission constants.
 * <p>
 * Defines all permissions related to Contact management.
 * Uses BasePermissions for standard operations.
 */
public final class ContactPermissions {

    private ContactPermissions() {
    }

    public static final String PREFIX = "contact";
    
    // Standard CRUD operations
    public static final String CREATE = PREFIX + "." + BasePermissions.OPERATION_CREATE;
    public static final String UPDATE = PREFIX + "." + BasePermissions.OPERATION_UPDATE;
    public static final String VIEW = PREFIX + "." + BasePermissions.OPERATION_VIEW;
    public static final String DELETE = PREFIX + "." + BasePermissions.OPERATION_DELETE;
}
