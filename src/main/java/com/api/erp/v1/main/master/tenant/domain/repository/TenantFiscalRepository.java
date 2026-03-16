package com.api.erp.v1.main.master.tenant.domain.repository;

import com.api.erp.v1.main.shared.domain.valueobject.CNPJ;
import com.api.erp.v1.main.master.tenant.domain.entity.TenantFiscal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository para TenantFiscal
 */
@Repository
public interface TenantFiscalRepository extends JpaRepository<TenantFiscal, Long> {
    
    Optional<TenantFiscal> findByTenantId(Long tenantId);
    boolean existsByCnpj(CNPJ cnpj);
}
