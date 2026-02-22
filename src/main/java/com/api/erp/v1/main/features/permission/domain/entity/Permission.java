package com.api.erp.v1.main.features.permission.domain.entity;

import com.api.erp.v1.main.shared.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_permission")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Permission extends BaseEntity {

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
