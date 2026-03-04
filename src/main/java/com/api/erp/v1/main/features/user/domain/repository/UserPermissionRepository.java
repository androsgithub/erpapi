//package com.api.erp.v1.main.features.user.domain.repository;
//
//import com.api.erp.v1.main.features.user.domain.entity.UserPermission;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//@Repository
//public interface UserPermissionRepository extends JpaRepository<UserPermission, Long> {
//
//    // ⚠️ Queries com tenantGroupId null precisam usar IS NULL em SQL
//    // Suporta tenants com e without group partitioning
//    @Query("""
//            SELECT CASE WHEN COUNT(up) > 0 THEN true ELSE false END
//            FROM UserPermission up
//            WHERE up.user.id = :userId
//              AND up.permission.id = :permissionId
//              AND up.tenantId = :tenantId
//              AND (
//                (:tenantGroupId IS NULL AND up.tenantGroupId IS NULL)
//                OR up.tenantGroupId = :tenantGroupId
//              )
//            """)
//    boolean existsByUserIdAndPermissionIdAndTenantIdAndTenantGroupId(
//            @Param("userId") Long userId,
//            @Param("permissionId") Long permissionId,
//            @Param("tenantId") Long tenantId,
//            @Param("tenantGroupId") Long tenantGroupId
//    );
//}
//
