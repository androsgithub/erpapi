package com.api.erp.v1.main.features.permission.domain.entity;

import com.api.erp.v1.main.shared.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
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

    @OneToMany
    @JoinTable(
            name="tb_role_permission",
            joinColumns = @JoinColumn( name="role_id"),
            inverseJoinColumns = @JoinColumn( name="permission_id")
    )
    private Set<Permission> permissions;
}
