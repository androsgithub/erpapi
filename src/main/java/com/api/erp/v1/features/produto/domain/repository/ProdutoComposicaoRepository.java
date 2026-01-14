package com.api.erp.v1.features.produto.domain.repository;

import com.api.erp.v1.features.produto.domain.entity.ProdutoComposicao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório de ProdutoComposicao
 */
@Repository
public interface ProdutoComposicaoRepository extends JpaRepository<ProdutoComposicao, Long> {
    
    /**
     * Lista todos os componentes de um produto fabricado
     */
    List<ProdutoComposicao> findByProdutoFabricadoId(Long produtoFabricadoId);
    
    /**
     * Verifica se um produto é usado como componente de outro
     */
    boolean existsByProdutoComponenteId(Long produtoComponenteId);
    
    /**
     * Busca a composição entre dois produtos específicos
     */
    Optional<ProdutoComposicao> findByProdutoFabricadoIdAndProdutoComponenteId(
        Long produtoFabricadoId, 
        Long produtoComponenteId
    );
    
    /**
     * Remove todas as composições de um produto
     */
    void deleteByProdutoFabricadoId(Long produtoFabricadoId);
}
