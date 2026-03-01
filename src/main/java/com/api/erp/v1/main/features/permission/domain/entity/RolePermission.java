//package com.api.erp.v1.main.features.permission.domain.entity;
//
//import com.api.erp.v1.main.shared.domain.entity.BaseEntity;
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.Setter;
//
//@Entity
//@Table(name = "tb_role_permission")
//@Getter
//@Setter
//public class RolePermission extends BaseEntity {
//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "role_id")
//    private Role role;
//
//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "permission_id")
//    private Permission permission;
//}
//
