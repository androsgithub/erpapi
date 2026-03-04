package com.api.erp.v1.main.features.permission.domain.entity;

/**
 * Role-specific permission constants.
 * <p>
 * Defines all permissions related to Role management.
 * Uses BasePermissions for standard operations.
 */
public final class RolePermissions {

    private RolePermissions() {
    }

    public static final String PREFIX = "role";
    
    // Standard CRUD operations
    public static final String CREATE = PREFIX + "." + BasePermissions.OPERATION_CREATE;
    public static final String UPDATE = PREFIX + "." + BasePermissions.OPERATION_UPDATE;
    public static final String VIEW = PREFIX + "." + BasePermissions.OPERATION_VIEW;
    public static final String DELETE = PREFIX + "." + BasePermissions.OPERATION_DELETE;
    
    // Role-specific operations
    public static final String ASSIGN = PREFIX + "." + BasePermissions.OPERATION_ASSIGN;
}
