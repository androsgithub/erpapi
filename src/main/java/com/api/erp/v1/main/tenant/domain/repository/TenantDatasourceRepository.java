package com.api.erp.v1.main.tenant.domain.repository;

import com.api.erp.v1.main.tenant.domain.entity.TenantDatasource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * TenantDatasourceRepository
 * <p>
 * Repository para gerenciar configurações de datasources dos tenants
 */
@Repository
public interface TenantDatasourceRepository extends JpaRepository<TenantDatasource, Long> {

    /**
     * Busca datasource ativo por tenantId
     */
    Optional<TenantDatasource> findByTenant_IdAndIsActive(Long tenantId, Boolean isActive);

    /**
     * Busca datasource ativo por tenantSlug
     * (Necessário criar slug no Tenant ou usar findByTenant_Nome)
     */
    @Query("SELECT td FROM TenantDatasource td " +
            "WHERE td.tenant.nome = :tenantSlug AND td.isActive = true")
    Optional<TenantDatasource> findByTenant_SlugAndIsActive(@Param("tenantSlug") String tenantSlug,
                                                            @Param("isActive") Boolean isActive);

    /**
     * Busca datasource por tenantId (independente de ativo)
     */
    Optional<TenantDatasource> findByTenant_Id(Long tenantId);

    /**
     * Verifica se existe datasource configurado para um tenant
     */
    boolean existsByTenant_IdAndIsActive(Long tenantId, Boolean isActive);

    /**
     * Busca datasource ativo por tenantId e status
     * Utilizado pelo TenantMigrationService
     */
    default TenantDatasource findByTenantIdAndStatus(Long tenantId, boolean status) {
        return findByTenant_IdAndIsActive(tenantId, status).orElse(null);
    }
}

