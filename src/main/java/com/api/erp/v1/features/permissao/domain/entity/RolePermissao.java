package com.api.erp.v1.features.permissao.domain.entity;

import com.api.erp.v1.shared.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tb_role_permissao")
@Getter
@Setter
public class RolePermissao extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permissao_id")
    private Permissao permissao;
}

