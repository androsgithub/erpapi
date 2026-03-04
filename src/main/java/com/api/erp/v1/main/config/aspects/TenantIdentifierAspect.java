package com.api.erp.v1.main.config.aspects;

import com.api.erp.v1.main.datasource.routing.TenantContext;
import com.api.erp.v1.main.tenant.domain.repository.TenantGroupRepository;
//import com.api.erp.v1.main.tenant.infrastructure.config.datasource.TenantContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Aspect
@Configuration
@Slf4j
public class TenantIdentifierAspect {

    @PersistenceContext
    private EntityManager entityManager;

    private final TenantGroupRepository tenantGroupRepository;

    @Autowired
    public TenantIdentifierAspect(TenantGroupRepository tenantGroupRepository) {
        this.tenantGroupRepository = tenantGroupRepository;
    }

    @Before("execution(* com.api.erp.v1.main.features..*(..))" + " || " +
            "execution(* com.api.erp.v1.main.shared.infrastructure.config.startup.seed..*(..))")
    public void setTenantIdentifier() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            log.warn("⚠️ TenantId not found in context");
            return;
        }

        // Fetch all TenantGroups containing this tenant
        // TODO: In the future enable filter by groupIds when data compartmentalization is implemented
        List<Long> groupIds = tenantGroupRepository.findGroupIdsByTenantId(tenantId);

        // ✅ Set GROUP_IDS (lista) no TenantContext - sempre, mesmo que vazia
        TenantContext.setGroupIds(groupIds);
        
        // ✅ Set GROUP_ID (singular) no TenantContext APENAS se houver grupos
        // Para tenants without group partitioning, GROUP_ID fica null
        if (!groupIds.isEmpty()) {
            TenantContext.setGroupIds(groupIds);
            log.debug("✅ Tenant {} com grupos: {}", tenantId, groupIds);
        } else {
            log.debug("ℹ️ Tenant {} without group partitioning", tenantId);
        }

        Session session = entityManager.unwrap(Session.class);

        // ✅ Ativar filtro de tenant
        if (session.getEnabledFilter("tenantIdFilter") == null) {
            session.enableFilter("tenantIdFilter")
                    .setParameter("tenantId", tenantId);
            log.info("✅ Tenant filter applied. TenantId: {}", tenantId);
        }
    }
}