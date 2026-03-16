package com.api.erp.v1.main.master.tenant.domain.repository;

import com.api.erp.v1.main.master.tenant.domain.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {
    
    /**
     * Busca tenant por name (slug)
     */
    Optional<Tenant> findByName(String name);

    /**
     * Verifica se tenant está ativo
     */
    boolean existsByIdAndActiveTrue(Long id);

    /**
     * Busca todos os tenants ativos
     * Utilizado pelo TenantMigrationService para migrar todos os tenants
     */
    List<Tenant> findAllByActiveTrue();
}
