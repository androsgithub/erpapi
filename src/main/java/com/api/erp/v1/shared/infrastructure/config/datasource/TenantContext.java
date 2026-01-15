package com.api.erp.v1.shared.infrastructure.config.datasource;

public final class TenantContext {

    private static final ThreadLocal<Long> TENANT_ID = new ThreadLocal<>();

    public static void setTenantId(Long tenantId) {
        TENANT_ID.set(tenantId);
    }

    public static void setTenantId(String tenantId) {
        if (tenantId != null && !tenantId.isEmpty()) {
            try {
                TENANT_ID.set(Long.parseLong(tenantId));
            } catch (NumberFormatException e) {
                // Se não for um número válido, ignorar
            }
        }
    }

    public static Long getTenantId() {
        return TENANT_ID.get();
    }

    public static void clear() {
        TENANT_ID.remove();
    }
}

