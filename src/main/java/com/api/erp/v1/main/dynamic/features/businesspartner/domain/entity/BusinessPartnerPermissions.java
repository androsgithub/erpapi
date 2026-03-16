package com.api.erp.v1.main.dynamic.features.businesspartner.domain.entity;

import com.api.erp.v1.main.master.permission.domain.entity.BasePermissions;

/**
 * BusinessPartner-specific permission constants.
 * <p>
 * Defines all permissions related to BusinessPartner management.
 * Uses BasePermissions for standard operations.
 */
public final class BusinessPartnerPermissions {

    private BusinessPartnerPermissions() {
    }

    public static final String PREFIX = "businesspartner";
    
    // Standard CRUD operations
    public static final String CREATE = PREFIX + "." + BasePermissions.OPERATION_CREATE;
    public static final String UPDATE = PREFIX + "." + BasePermissions.OPERATION_UPDATE;
    public static final String VIEW = PREFIX + "." + BasePermissions.OPERATION_VIEW;
    public static final String DELETE = PREFIX + "." + BasePermissions.OPERATION_DELETE;
}
