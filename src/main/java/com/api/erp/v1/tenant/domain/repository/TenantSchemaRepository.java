package com.api.erp.v1.tenant.domain.repository;

import com.api.erp.v1.tenant.domain.entity.TenantSchema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TenantSchemaRepository extends JpaRepository<TenantSchema, Long> {
    Optional<TenantSchema> findByTenantIdAndIsActiveTrue(Long tenantId);
}
