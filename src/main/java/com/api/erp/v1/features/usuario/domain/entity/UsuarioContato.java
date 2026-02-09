package com.api.erp.v1.features.usuario.domain.entity;

import com.api.erp.v1.features.contato.domain.entity.Contato;
import com.api.erp.v1.shared.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tb_usuario_contato")
@Getter
@Setter
public class UsuarioContato extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contato_id")
    private Contato contato;
}

