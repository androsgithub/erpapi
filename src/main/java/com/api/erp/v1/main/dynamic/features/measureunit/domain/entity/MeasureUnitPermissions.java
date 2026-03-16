package com.api.erp.v1.main.dynamic.features.measureunit.domain.entity;

import com.api.erp.v1.main.master.permission.domain.entity.BasePermissions;

/**
 * Measure Unit-specific permission constants.
 * <p>
 * Defines all permissions related to Measure Unit management.
 * Uses BasePermissions for standard operations.
 */
public final class MeasureUnitPermissions {

    private MeasureUnitPermissions() {
    }

    public static final String PREFIX = "measure-unit";
    
    // Standard CRUD operations
    public static final String CREATE = PREFIX + "." + BasePermissions.OPERATION_CREATE;
    public static final String UPDATE = PREFIX + "." + BasePermissions.OPERATION_UPDATE;
    public static final String VIEW = PREFIX + "." + BasePermissions.OPERATION_VIEW;
    public static final String DELETE = PREFIX + "." + BasePermissions.OPERATION_DELETE;
    
    // Activation operations
    public static final String ACTIVATE = PREFIX + "." + BasePermissions.OPERATION_ACTIVATE;
    public static final String DEACTIVATE = PREFIX + "." + BasePermissions.OPERATION_DEACTIVATE;
}