package com.api.erp.v1.main.master.permission.domain.entity;

import com.api.erp.v1.main.shared.domain.entity.TenantScopeEntity;
import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_permission")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Permission extends TenantScopeEntity {

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String module;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PermissionAction action;
}
