package com.api.erp.v1.features.permissao.domain.entity;

import com.api.erp.v1.shared.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_permissao")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Permissao extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String codigo;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String modulo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoAcao acao;
}
