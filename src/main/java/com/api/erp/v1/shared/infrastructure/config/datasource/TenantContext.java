package com.api.erp.v1.shared.infrastructure.config.datasource;

public final class TenantContext {

    private static final ThreadLocal<String> TENANT_SLUG = new ThreadLocal<>();
    private static final ThreadLocal<String> TENANT_ID = new ThreadLocal<>();

    public static void setTenantSlug(String tenantSlug) {
        TENANT_SLUG.set(tenantSlug);
    }

    public static String getTenantSlug() {
        return TENANT_SLUG.get();
    }

    public static void setTenantId(String tenantId) {
        TENANT_ID.set(tenantId);
    }

    public static String getTenantId() {
        return TENANT_ID.get();
    }

    public static void clear() {
        TENANT_SLUG.remove();
        TENANT_ID.remove();
    }
}

