package com.api.erp.v1.main.features.product.domain.repository;

import com.api.erp.v1.main.features.product.domain.entity.Product;
import com.api.erp.v1.main.features.product.domain.entity.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório de Product
 * 
 * DIP: Depende de abstração (JpaRepository)
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    /**
     * Busca um product pelo código
     */
    Optional<Product> findByCodigo(String codigo);
    
    /**
     * Verifica se existe um product com o código informado
     */
    boolean existsByCodigo(String codigo);
    
    /**
     * Lista todos os products de um tipo específico
     */
    List<Product> findByType(ProductType type);
    
    /**
     * Lista todos os products ativos (status = ATIVO)
     */
    List<Product> findByStatusName(String statusName);
}
