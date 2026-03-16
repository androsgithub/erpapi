package com.api.erp.v1.main.master.permission.domain.repository;

import com.api.erp.v1.main.master.permission.domain.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByCodigo(String codigo);

    @Query(value = """
            SELECT COUNT(*)
            FROM (
                SELECT 1
                FROM tb_user_permission up
                JOIN tb_permission p
                    ON p.id = up.permission_id
                WHERE up.user_id = :userId
                  AND p.codigo = :permissionCodigo
                
                UNION
                
                SELECT 1
                FROM tb_user_role ur
                JOIN tb_role_permission rp
                    ON rp.role_id = ur.role_id
                JOIN tb_permission p
                    ON p.id = rp.permission_id
                WHERE ur.user_id = :userId
                  AND p.codigo = :permissionCodigo
            ) t
            """, nativeQuery = true)
    long countPermission(
            @Param("userId") Long userId,
            @Param("permissionCodigo") String permissionCodigo
    );
}
