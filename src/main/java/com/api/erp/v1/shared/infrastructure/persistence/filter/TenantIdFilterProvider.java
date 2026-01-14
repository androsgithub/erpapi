package com.api.erp.v1.shared.infrastructure.persistence.filter;

import com.api.erp.v1.shared.infrastructure.config.datasource.TenantContext;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

/**
 * TenantIdFilterProvider
 * 
 * Provedor que aplica filtros automáticos do Hibernate para discriminar dados por tenantId.
 * 
 * Isso permite que empresas que compartilhem o mesmo banco (ex: matriz + filiais)
 * tenham seus dados segregados via tenantId automaticamente.
 * 
 * Uso:
 * - Adicionar @FilterDef e @Filter nas entidades que precisam discriminação
 * - Este componente ativa os filtros automaticamente a cada session
 */
@Component
public class TenantIdFilterProvider {

    public static final String TENANT_ID_FILTER = "tenantIdFilter";
    public static final String TENANT_ID_PARAM = "tenantId";

    /**
     * Aplica filtro de tenantId na session
     */
    public static void enableTenantIdFilter(Session session) {
        String tenantId = TenantContext.getTenantId();
        
        if (tenantId != null && !tenantId.isEmpty()) {
            Filter filter = session.enableFilter(TENANT_ID_FILTER);
            filter.setParameter(TENANT_ID_PARAM, Long.parseLong(tenantId));
        }
    }

    /**
     * Desabilita filtro de tenantId na session
     */
    public static void disableTenantIdFilter(Session session) {
        session.disableFilter(TENANT_ID_FILTER);
    }
}
