//package com.api.erp.v1.main.tenant.infrastructure.config.datasource;
//
//import java.util.List;
//
///**
// * ⚠️ DEPRECATED - Use TenantContext em com.api.erp.v1.main.datasource.routing
// *
// * Esta classe foi substituída pela arquitetura consolidada e limpa.
// * A nova implementação usa String para tenantId em vez de Long, com suporte a RequestContextHolder.
// *
// * Para background tasks, pode usar: com.api.erp.v1.main.datasource.routing.TenantContext
// * Para requisições HTTP, use TenantContextProvider com RequestContextHolder.
// *
// * IMPORTANTE: Considere migrar referências deste TenantContext para o novo.
// */
//@Deprecated(since = "2.0", forRemoval = true)
//public final class TenantContext {
//
//    private static final ThreadLocal<Long> TENANT_ID = new ThreadLocal<>();
//    private static final ThreadLocal<Long> GROUP_ID = new ThreadLocal<>();
//    private static final ThreadLocal<List<Long>> GROUP_IDS = new ThreadLocal<>();
//
//    public static void setTenantId(Long tenantId) {
//        TENANT_ID.set(tenantId);
//    }
//
//    public static Long getTenantId() {
//        return TENANT_ID.get();
//    }
//
//    public static void setGroupId(Long groupId) {
//        GROUP_ID.set(groupId);
//    }
//
//    public static Long getGroupId() {
//        return GROUP_ID.get();
//    }
//
//    public static void setGroupIds(List<Long> groupIds) {
//        GROUP_IDS.set(groupIds);
//    }
//
//    public static List<Long> getGroupIds() {
//        return GROUP_IDS.get();
//    }
//
//    public static void clear() {
//        TENANT_ID.remove();
//        GROUP_ID.remove();
//        GROUP_IDS.remove();
//    }
//}