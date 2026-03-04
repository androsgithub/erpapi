//package com.api.erp.v1.main.features.user.domain.entity;
//
//import com.api.erp.v1.main.features.permission.domain.entity.Permission;
//import com.api.erp.v1.main.shared.domain.entity.BaseEntity;
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.Setter;
//
//@Entity
//@Table(name = "tb_user_permission")
//@Getter
//@Setter
//public class UserPermission extends BaseEntity {
//
//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "user_id")
//    private User user;
//
//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "permission_id")
//    private Permission permission;
//}
