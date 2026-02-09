package com.api.erp.v1.features.usuario.domain.entity;

import com.api.erp.v1.features.permissao.domain.entity.Role;
import com.api.erp.v1.shared.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tb_usuario_role")
@Getter
@Setter
public class UsuarioRole extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;
}
