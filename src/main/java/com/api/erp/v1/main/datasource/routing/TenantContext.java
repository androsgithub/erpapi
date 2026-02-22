package com.api.erp.v1.main.datasource.routing;

import java.util.List;

/**
 * UTILITY - Contexto de Tenant com suporte a ThreadLocal
 * 
 * Armazena informações do tenant para a thread atual.
 * Compatível com estrutura anterior do sistema.
 * 
 * Para requisições HTTP, usar TenantContextProvider (via RequestContextHolder).
 * Para background tasks, pode usar este contexto diretamente.
 * 
 * @author ERP System
 * @version 1.0
 */
public final class TenantContext {

    private static final ThreadLocal<Long> TENANT_ID = new ThreadLocal<>();
    private static final ThreadLocal<Long> GROUP_ID = new ThreadLocal<>();
    private static final ThreadLocal<List<Long>> GROUP_IDS = new ThreadLocal<>();

    private TenantContext() {
        // Classe utilitária
    }

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
