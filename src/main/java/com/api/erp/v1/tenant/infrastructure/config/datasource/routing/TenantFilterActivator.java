package com.api.erp.v1.tenant.infrastructure.config.datasource.routing;

import com.api.erp.v1.tenant.infrastructure.config.datasource.TenantContext;
import com.api.erp.v1.shared.infrastructure.persistence.filter.TenantIdFilterProvider;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

/**
 * TenantFilterActivator
 * 
 * Componente responsável por ativar o filtro de tenantId no EntityManager.
 * Obtém o EntityManager via @PersistenceContext (injeção automática do Spring).
 */
@Slf4j
@Component
public class TenantFilterActivator {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Ativa o filtro de tenantId na sessão Hibernate atual.
     * O EntityManager é injetado via @PersistenceContext.
     */
    public void activateTenantFilter() {
        try {
            Long tenantId = TenantContext.getTenantId();
            
            if (tenantId != null) {
                Session session = entityManager.unwrap(Session.class);
                TenantIdFilterProvider.enableTenantIdFilter(session);
                log.debug("✅ Filtro de tenantId ativado para tenant ID: {}", tenantId);
            } else {
                log.debug("⚠️ TenantContext.tenantId está vazio, filtro não ativado");
            }
        } catch (Exception e) {
            log.warn("⚠️ Erro ao ativar filtro de tenantId", e);
        }
    }

    /**
     * Desativa o filtro de tenantId na sessão Hibernate atual.
     */
    public void deactivateTenantFilter() {
        try {
            Session session = entityManager.unwrap(Session.class);
            TenantIdFilterProvider.disableTenantIdFilter(session);
            log.debug("🔓 Filtro de tenantId desativado");
        } catch (Exception e) {
            log.debug("Erro ao desativar filtro de tenantId", e);
        }
    }
}
