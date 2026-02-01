package com.api.erp.v1.features.permissao.domain.entity;

import com.api.erp.v1.shared.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Table(name = "tb_role")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Role extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String nome;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "tb_role_permissao",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permissao_id")
    )
    private Set<Permissao> permissoes;
}
