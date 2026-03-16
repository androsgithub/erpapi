package com.api.erp.v1.main.master.tenant.domain.repository;

import com.api.erp.v1.main.master.tenant.domain.entity.TenantAllowDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para TenantAllowDomain
 */
@Repository
public interface TenantAllowDomainRepository extends JpaRepository<TenantAllowDomain, Long> {
    
    List<TenantAllowDomain> findByTenantIdAndActiveTrue(Long tenantId);
    
    List<TenantAllowDomain> findByTenantId(Long tenantId);
    
    Optional<TenantAllowDomain> findByTenantIdAndDomainAndActiveTrue(Long tenantId, String domain);
}
