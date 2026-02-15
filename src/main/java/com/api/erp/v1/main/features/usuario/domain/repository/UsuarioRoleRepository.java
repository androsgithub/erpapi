package com.api.erp.v1.main.features.usuario.domain.repository;

import com.api.erp.v1.main.features.permissao.domain.entity.Role;
import com.api.erp.v1.main.features.usuario.domain.entity.UsuarioRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UsuarioRoleRepository extends JpaRepository<UsuarioRole, Long> {

    // ⚠️ Spring Data não consegue lidar bem com null em predicados = 
    // Por isto, prefira as queries @Query abaixo que usam IS NULL quando necessário

    @Query("""
            SELECT CASE WHEN COUNT(ur) > 0 THEN true ELSE false END
            FROM UsuarioRole ur
            WHERE ur.usuario.id = :usuarioId
              AND ur.role.id = :roleId
              AND ur.tenantId = :tenantId
              AND (
                (:tenantGroupId IS NULL AND ur.tenantGroupId IS NULL)
                OR ur.tenantGroupId = :tenantGroupId
              )
            """)
    boolean existsByUsuarioIdAndRoleIdAndTenantIdAndTenantGroupId(
            Long usuarioId,
            Long roleId,
            Long tenantId,
            Long tenantGroupId
    );

    @Query("""
            SELECT ur
            FROM UsuarioRole ur
            WHERE ur.usuario.id = :usuarioId
              AND ur.role.id = :roleId
              AND ur.tenantId = :tenantId
              AND (
                (:tenantGroupId IS NULL AND ur.tenantGroupId IS NULL)
                OR ur.tenantGroupId = :tenantGroupId
              )
            """)
    Optional<UsuarioRole> findByUsuarioIdAndRoleIdAndTenantIdAndTenantGroupId(
            Long usuarioId,
            Long roleId,
            Long tenantId,
            Long tenantGroupId
    );

    @Query("""
                select ur.role
                from UsuarioRole ur
                where ur.usuario.id = :usuarioId
                  and ur.tenantId = :tenantId
                  and (
                    (:tenantGroupId IS NULL AND ur.tenantGroupId IS NULL)
                    OR ur.tenantGroupId = :tenantGroupId
                  )
            """)
    List<Role> listarRoles(
            Long usuarioId,
            Long tenantId,
            Long tenantGroupId
    );

}

