package com.api.erp.v1.main.master.permission.domain.entity;

import com.api.erp.v1.main.shared.domain.entity.TenantScopeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_role")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Role extends TenantScopeEntity {

    @Column(nullable = false)
    private String name;

    @OneToMany
    @JoinTable(
            name = "tb_role_permission",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    @Builder.Default
    private Set<Permission> permissions = new HashSet<>();
}
