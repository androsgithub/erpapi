package com.api.erp.v1.main.master.user.domain.entity;

import com.api.erp.v1.main.master.permission.domain.entity.BasePermissions;

/**
 * User-specific permission constants.
 * <p>
 * Defines all permissions related to User management.
 * Uses BasePermissions for standard operations.
 */
public final class UserPermissions {

    private UserPermissions() {
    }

    public static final String PREFIX = "user";
    
    // Standard CRUD operations
    public static final String CREATE = PREFIX + "." + BasePermissions.OPERATION_CREATE;
    public static final String UPDATE = PREFIX + "." + BasePermissions.OPERATION_UPDATE;
    public static final String LIST = PREFIX + "." + BasePermissions.OPERATION_LIST;
    public static final String LIST_ROLES = PREFIX + ".list_roles";
    public static final String LIST_PERMISSIONS = PREFIX + ".list_permissions";
    public static final String VIEW_OTHER_USER = PREFIX + ".view_other_user";
    public static final String DELETE = PREFIX + "." + BasePermissions.OPERATION_DELETE;
    
    // User activation/deactivation
    public static final String ACTIVATE = PREFIX + "." + BasePermissions.OPERATION_ACTIVATE;
    public static final String DEACTIVATE = PREFIX + "." + BasePermissions.OPERATION_DEACTIVATE;
    
    // Workflow operations
    public static final String APPROVE = PREFIX + "." + BasePermissions.OPERATION_APPROVE;
    public static final String REJECT = PREFIX + "." + BasePermissions.OPERATION_REJECT;

    // Permission management
    public static final String MANAGE_PERMISSIONS = PREFIX + "." + BasePermissions.OPERATION_MANAGE + ".permissions";
    public static final String ADD_PERMISSION = PREFIX + "." + BasePermissions.OPERATION_ADD + ".permission";
    public static final String REMOVE_PERMISSION = PREFIX + "." + BasePermissions.OPERATION_REMOVE + ".permission";

    // Role management
    public static final String MANAGE_ROLES = PREFIX + "." + BasePermissions.OPERATION_MANAGE + ".roles";
    public static final String ADD_ROLE = PREFIX + "." + BasePermissions.OPERATION_ADD + ".role";
    public static final String REMOVE_ROLE = PREFIX + "." + BasePermissions.OPERATION_REMOVE + ".role";
}