package com.api.erp.v1.config.aspects;

import com.api.erp.v1.tenant.infrastructure.config.datasource.TenantContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Session;
import org.springframework.context.annotation.Configuration;

@Aspect
@Configuration
@Slf4j
public class TenantIdentifierAspect {

    @PersistenceContext
    private EntityManager entityManager;

    @Before("execution(* com.api.erp.v1.features..*(..))" + " || "+
            "execution(* com.api.erp.v1.shared.infrastructure.config.startup.seed..*(..))" )
    public void setTenantIdentifier() {
        Long tenantId = TenantContext.getTenantId();

        if (tenantId == null) {
            log.warn("⚠️ TenantId não encontrado no contexto");
            return;
        }

        Session session = entityManager.unwrap(Session.class);

        if (session.getEnabledFilter("tenantIdFilter") == null) {
            session.enableFilter("tenantIdFilter")
                    .setParameter("tenantId", tenantId);
            log.info("✅ Tenant filter aplicado: {}", tenantId);
        }
    }
}

