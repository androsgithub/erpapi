package com.api.erp.v1.main.features.produto.domain.repository;

import com.api.erp.v1.main.features.produto.domain.entity.Produto;
import com.api.erp.v1.main.features.produto.domain.entity.TipoProduto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório de Produto
 * 
 * DIP: Depende de abstração (JpaRepository)
 */
@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    
    /**
     * Busca um produto pelo código
     */
    Optional<Produto> findByCodigo(String codigo);
    
    /**
     * Verifica se existe um produto com o código informado
     */
    boolean existsByCodigo(String codigo);
    
    /**
     * Lista todos os produtos de um tipo específico
     */
    List<Produto> findByTipo(TipoProduto tipo);
    
    /**
     * Lista todos os produtos ativos (status = ATIVO)
     */
    List<Produto> findByStatusName(String statusName);
}
