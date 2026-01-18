package com.api.erp.v1.tenant.domain.repository;

import com.api.erp.v1.tenant.domain.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {
    
    /**
     * Busca tenant por nome (slug)
     */
    Optional<Tenant> findByNome(String nome);

    /**
     * Verifica se tenant está ativo
     */
    boolean existsByIdAndAtivaTrue(Long id);

    /**
     * Busca todos os tenants ativos
     * Utilizado pelo TenantMigrationService para migrar todos os tenants
     */
    List<Tenant> findAllByAtivaTrue();

    /**
     * Busca tenant por slug (alias para nome)
     */
    default Tenant findBySlug(String slug) {
        return findByNome(slug).orElse(null);
    }
}
