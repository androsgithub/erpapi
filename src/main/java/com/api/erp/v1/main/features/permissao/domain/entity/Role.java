package com.api.erp.v1.main.features.permissao.domain.entity;

import com.api.erp.v1.main.shared.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
    @JoinTable(name = "tb_role_permissao", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "permissao_id"))
    private List<Permissao> permissoes;
}
