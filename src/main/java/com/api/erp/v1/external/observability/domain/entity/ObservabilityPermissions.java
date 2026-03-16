package com.api.erp.v1.external.observability.domain.entity;

import com.api.erp.v1.main.master.permission.domain.entity.BasePermissions;

/**
 * Observability-specific permission constants.
 * <p>
 * Defines all permissions related to Observability features.
 * Uses BasePermissions for standard operations.
 */
public final class ObservabilityPermissions {

    private ObservabilityPermissions() {
    }

    public static final String PREFIX = "observability";

    // View operation
    public static final String VIEW = PREFIX + "." + BasePermissions.OPERATION_VIEW;
}