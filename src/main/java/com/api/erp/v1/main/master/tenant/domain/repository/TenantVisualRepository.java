package com.api.erp.v1.main.master.tenant.domain.repository;

import com.api.erp.v1.main.master.tenant.domain.entity.TenantVisual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository para TenantVisual
 */
@Repository
public interface TenantVisualRepository extends JpaRepository<TenantVisual, Long> {
    
    Optional<TenantVisual> findByTenantId(Long tenantId);
}
