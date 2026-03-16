package com.api.erp.v1.main.master.permission.domain.entity;

import com.api.erp.v1.main.shared.domain.entity.TenantScopeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_role")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Role extends TenantScopeEntity {

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
