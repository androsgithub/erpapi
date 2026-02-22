package com.api.erp.v1.main.features.user.domain.repository;

import com.api.erp.v1.main.features.permission.domain.entity.Role;
import com.api.erp.v1.main.features.user.domain.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    // ⚠️ Spring Data não consegue lidar bem com null em predicados = 
    // Por isto, prefira as queries @Query abaixo que usam IS NULL quando necessário

    @Query("""
            SELECT CASE WHEN COUNT(ur) > 0 THEN true ELSE false END
            FROM UserRole ur
            WHERE ur.user.id = :userId
              AND ur.role.id = :roleId
              AND ur.tenantId = :tenantId
              AND (
                (:tenantGroupId IS NULL AND ur.tenantGroupId IS NULL)
                OR ur.tenantGroupId = :tenantGroupId
              )
            """)
    boolean existsByUserIdAndRoleIdAndTenantIdAndTenantGroupId(
            Long userId,
            Long roleId,
            Long tenantId,
            Long tenantGroupId
    );

    @Query("""
            SELECT ur
            FROM UserRole ur
            WHERE ur.user.id = :userId
              AND ur.role.id = :roleId
              AND ur.tenantId = :tenantId
              AND (
                (:tenantGroupId IS NULL AND ur.tenantGroupId IS NULL)
                OR ur.tenantGroupId = :tenantGroupId
              )
            """)
    Optional<UserRole> findByUserIdAndRoleIdAndTenantIdAndTenantGroupId(
            Long userId,
            Long roleId,
            Long tenantId,
            Long tenantGroupId
    );

    @Query("""
                select ur.role
                from UserRole ur
                where ur.user.id = :userId
                  and ur.tenantId = :tenantId
                  and (
                    (:tenantGroupId IS NULL AND ur.tenantGroupId IS NULL)
                    OR ur.tenantGroupId = :tenantGroupId
                  )
            """)
    List<Role> listarRoles(
            Long userId,
            Long tenantId,
            Long tenantGroupId
    );

}

