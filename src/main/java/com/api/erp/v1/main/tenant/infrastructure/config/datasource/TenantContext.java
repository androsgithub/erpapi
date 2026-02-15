package com.api.erp.v1.main.tenant.infrastructure.config.datasource;

import java.util.List;

public final class TenantContext {

    private static final ThreadLocal<Long> TENANT_ID = new ThreadLocal<>();
    private static final ThreadLocal<Long> GROUP_ID = new ThreadLocal<>();
    private static final ThreadLocal<List<Long>> GROUP_IDS = new ThreadLocal<>();

    public static void setTenantId(Long tenantId) {
        TENANT_ID.set(tenantId);
    }

    public static Long getTenantId() {
        return TENANT_ID.get();
    }

    public static void setGroupId(Long groupId) {
        GROUP_ID.set(groupId);
    }

    public static Long getGroupId() {
        return GROUP_ID.get();
    }

    public static void setGroupIds(List<Long> groupIds) {
        GROUP_IDS.set(groupIds);
    }

    public static List<Long> getGroupIds() {
        return GROUP_IDS.get();
    }

    public static void clear() {
        TENANT_ID.remove();
        GROUP_ID.remove();
        GROUP_IDS.remove();
    }
}