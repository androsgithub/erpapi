package com.api.erp.v1.main.master.tenant.domain.repository;

import com.api.erp.v1.main.master.tenant.domain.entity.TenantFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para TenantFeature
 * <p>
 * Responsável por recuperar os bean overrides por tenant.
 */
@Repository
public interface TenantFeatureRepository extends JpaRepository<TenantFeature, Long> {
    
    /**
     * Encontra o bean override para um tenant e feature específica (apenas ativo)
     * <p>
     * Método central para o TenantBeanResolver.
     *
     * @param tenantId    ID do tenant
     * @param featureKey  chave da feature (ex: "userService")
     * @return Optional com o TenantFeature se ativo, vazio caso contrário
     */
    Optional<TenantFeature> findByTenantIdAndFeatureKeyAndActiveTrue(Long tenantId, String featureKey);
    
    /**
     * Lista todas as features ativas de um tenant
     */
    List<TenantFeature> findByTenantIdAndActiveTrue(Long tenantId);
    
    /**
     * Lista todas as features de um tenant (ativas e inativas)
     */
    List<TenantFeature> findByTenantId(Long tenantId);
}
