package com.api.erp.v1.main.shared.domain.enums;

public enum TenantAccessType {

    PUBLIC(false, false),
    PUBLIC_WITH_TENANT(false, true),

    AUTHENTICATED(true, false),
    AUTHENTICATED_WITH_TENANT(true, true);

    private final boolean requiresAuth;
    private final boolean requiresTenant;

    TenantAccessType(boolean requiresAuth, boolean requiresTenant) {
        this.requiresAuth = requiresAuth;
        this.requiresTenant = requiresTenant;
    }

    public boolean requiresAuth() {
        return requiresAuth;
    }

    public boolean requiresTenant() {
        return requiresTenant;
    }
}

