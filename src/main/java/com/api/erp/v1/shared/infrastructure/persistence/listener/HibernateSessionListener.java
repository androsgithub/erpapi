package com.api.erp.v1.shared.infrastructure.persistence.listener;

import com.api.erp.v1.shared.infrastructure.persistence.filter.TenantIdFilterProvider;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.springframework.context.annotation.Configuration;

/**
 * HibernateSessionListener
 * 
 * Listener que intercepta abertura de sessões Hibernate e aplica
 * filtros de tenantId automaticamente.
 * 
 * Nota: Este será usado em um interceptor de JPA se necessário.
 */
@Slf4j
@Configuration
public class HibernateSessionListener {

    /**
     * Aplica filtros ao abrir sessão
     */
    public static void onSessionOpen(Session session) {
        TenantIdFilterProvider.enableTenantIdFilter(session);
        log.debug("Filtros de tenantId aplicados à sessão");
    }

    /**
     * Remove filtros ao fechar sessão
     */
    public static void onSessionClose(Session session) {
        try {
            TenantIdFilterProvider.disableTenantIdFilter(session);
        } catch (Exception e) {
            log.debug("Erro ao desabilitar filtro", e);
        }
    }
}
