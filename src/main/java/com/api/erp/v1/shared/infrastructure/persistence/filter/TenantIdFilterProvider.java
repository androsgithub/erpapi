package com.api.erp.v1.shared.infrastructure.persistence.filter;

import com.api.erp.v1.tenant.infrastructure.config.datasource.TenantContext;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class TenantIdFilterProvider {

    public static final String TENANT_ID_FILTER = "tenantIdFilter";
    public static final String TENANT_ID_PARAM = "tenantId";

    /**
     * Aplica filtro de tenantId na session
     */
    public static void enableTenantIdFilter(Session session) {
        Long tenantId = TenantContext.getTenantId();
        log.info("🔐 [TenantIdFilterProvider] Tentando ativar filtro. TenantContext.tenantId = {}", tenantId);

        if (tenantId != null) {
            Filter filter = session.enableFilter(TENANT_ID_FILTER);
            filter.setParameter(TENANT_ID_PARAM, tenantId);
            log.info("✅ [TenantIdFilterProvider] Filtro ATIVADO! tenantIdFilter com parâmetro tenantId = {}", tenantId);
        } else {
            log.warn("⚠️ [TenantIdFilterProvider] TenantId é NULL - filtro NÃO ativado!");
        }
    }

    /**
     * Desabilita filtro de tenantId na session
     */
    public static void disableTenantIdFilter(Session session) {
        log.debug("🔓 [TenantIdFilterProvider] Desativando filtro de tenantId");
        session.disableFilter(TENANT_ID_FILTER);
    }
}
