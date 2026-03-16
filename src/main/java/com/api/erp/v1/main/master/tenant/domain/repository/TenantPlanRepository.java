package com.api.erp.v1.main.master.tenant.domain.repository;

import com.api.erp.v1.main.master.tenant.domain.entity.TenantPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository para TenantPlan
 */
@Repository
public interface TenantPlanRepository extends JpaRepository<TenantPlan, Long> {
    
}
