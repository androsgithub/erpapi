package com.api.erp.v1.main.master.tenant.domain.repository;

import com.api.erp.v1.main.master.tenant.domain.entity.TenantConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository para TenantConfig
 */
@Repository
public interface TenantConfigRepository extends JpaRepository<TenantConfig, Long> {
    
    Optional<TenantConfig> findByTenantId(Long tenantId);
}
