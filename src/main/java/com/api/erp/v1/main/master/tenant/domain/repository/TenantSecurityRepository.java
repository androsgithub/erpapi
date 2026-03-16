package com.api.erp.v1.main.master.tenant.domain.repository;

import com.api.erp.v1.main.master.tenant.domain.entity.TenantSecurity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository para TenantSecurity
 */
@Repository
public interface TenantSecurityRepository extends JpaRepository<TenantSecurity, Long> {
    
    Optional<TenantSecurity> findByTenantId(Long tenantId);
}
