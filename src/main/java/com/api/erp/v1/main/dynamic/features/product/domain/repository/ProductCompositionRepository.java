package com.api.erp.v1.main.dynamic.features.product.domain.repository;

import com.api.erp.v1.main.dynamic.features.product.domain.entity.ProductComposition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório de ProductComposition
 */
@Repository
public interface ProductCompositionRepository extends JpaRepository<ProductComposition, Long> {
    
    /**
     * Lista todos os componentes de um product fabricado
     */
    List<ProductComposition> findByProductFabricadoId(Long productFabricadoId);
    
    /**
     * Verifica se um product é usado como componente de outro
     */
    boolean existsByProductComponenteId(Long productComponenteId);
    
    /**
     * Busca a composição entre dois products específicos
     */
    Optional<ProductComposition> findByProductFabricadoIdAndProductComponenteId(
        Long productFabricadoId, 
        Long productComponenteId
    );
    
    /**
     * Remove todas as composições de um product
     */
    void deleteByProductFabricadoId(Long productFabricadoId);
}
