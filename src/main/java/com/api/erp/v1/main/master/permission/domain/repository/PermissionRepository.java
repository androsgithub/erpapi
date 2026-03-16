package com.api.erp.v1.main.master.permission.domain.repository;

import com.api.erp.v1.main.master.permission.domain.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByCode(String code);

    @Query(value = """
            SELECT COUNT(*)
            FROM (
                SELECT 1
                FROM TB_USER_PERMISSION UP
                JOIN TB_PERMISSION P
                    ON P.ID = UP.PERMISSION_ID
                WHERE UP.USER_ID = :userId
                  AND P.CODE = :permissionCode
            
                UNION
            
                SELECT 1
                FROM TB_USER_ROLE UR
                JOIN TB_ROLE_PERMISSION RP
                    ON RP.ROLE_ID = UR.ROLE_ID
                JOIN TB_PERMISSION P
                    ON P.ID = RP.PERMISSION_ID
                WHERE UR.USER_ID = :userId
                  AND P.CODE = :permissionCode
            ) T
            """, nativeQuery = true)
    long countPermission(
            @Param("userId") Long userId,
            @Param("permissionCode") String permissionCode
    );
}
