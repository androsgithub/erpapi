package com.api.erp.v1.main.features.usuario.domain.entity;

import com.api.erp.v1.main.features.permissao.domain.entity.Permissao;
import com.api.erp.v1.main.shared.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tb_usuario_permissao")
@Getter
@Setter
public class UsuarioPermissao extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permissao_id")
    private Permissao permissao;
}
