package com.api.erp.v1.main.tenant.domain.repository;

import com.api.erp.v1.main.tenant.domain.entity.TenantGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TenantGroupRepository extends JpaRepository<TenantGroup, Long> {
    @Query("SELECT tg.id FROM TenantGroup tg " +
            "INNER JOIN tg.tenants t " +
            "WHERE t.id = :tenantId")
    List<Long> findGroupIdsByTenantId(@Param("tenantId") Long tenantId);
}
