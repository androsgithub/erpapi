package com.api.erp.v1.main.features.measureunit.domain.repository;

import com.api.erp.v1.main.features.measureunit.domain.entity.MeasureUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório da entidade MeasureUnit.
 * 
 * DIP: Depende de abstrações (JpaRepository)
 * Responsabilidade única: Persistência da MeasureUnit
 */
@Repository
public interface MeasureUnitRepository extends JpaRepository<MeasureUnit, Long> {
    
    /**
     * Busca uma unidade de medida pela sigla
     */
    Optional<MeasureUnit> findBySigla(String sigla);
    
    /**
     * Verifica se existe uma unidade com a sigla informada
     */
    boolean existsBySigla(String sigla);
}
