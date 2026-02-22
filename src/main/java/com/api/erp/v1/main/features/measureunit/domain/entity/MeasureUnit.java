package com.api.erp.v1.main.features.measureunit.domain.entity;

import com.api.erp.v1.main.shared.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entidade de domínio que representa uma Unidade de Medida.
 * <p>
 * Responsabilidades:
 * - Armazenar dados da unidade de medida
 * - Validar estado da entidade através de métodos de comportamento
 * <p>
 * SRP: Uma única responsabilidade - representar uma unidade de medida
 */

@Entity
@Table(name = "tb_measure_unit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeasureUnit extends BaseEntity {

    @Column(nullable = false, unique = true, length = 10)
    private String sigla;

    @Column(nullable = false, length = 100)
    private String descricao;
}
