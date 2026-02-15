package com.api.erp.v1.main.features.unidademedida.domain.repository;

import com.api.erp.v1.main.features.unidademedida.domain.entity.UnidadeMedida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório da entidade UnidadeMedida.
 * 
 * DIP: Depende de abstrações (JpaRepository)
 * Responsabilidade única: Persistência da UnidadeMedida
 */
@Repository
public interface UnidadeMedidaRepository extends JpaRepository<UnidadeMedida, Long> {
    
    /**
     * Busca uma unidade de medida pela sigla
     */
    Optional<UnidadeMedida> findBySigla(String sigla);
    
    /**
     * Verifica se existe uma unidade com a sigla informada
     */
    boolean existsBySigla(String sigla);
}
