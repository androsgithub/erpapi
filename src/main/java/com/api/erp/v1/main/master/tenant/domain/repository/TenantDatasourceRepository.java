package com.api.erp.v1.main.master.tenant.domain.repository;

import com.api.erp.v1.main.master.tenant.domain.entity.TenantDatasource;
import org.springframework.data.jpa.repository.JpaRepository;
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
    Optional<TenantDatasource> findByTenantIdAndActiveTrue(Long tenantId);

    /**
     * Busca datasource por tenantId (independente de ativo)
     */
    Optional<TenantDatasource> findByTenantId(Long tenantId);

    /**
     * Verifica se existe datasource configurado para um tenant
     */
    boolean existsByTenantIdAndActiveTrue(Long tenantId);
}


