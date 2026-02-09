package com.api.erp.v1.features.usuario.domain.repository;

import com.api.erp.v1.features.usuario.domain.entity.UsuarioPermissao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioPermissaoRepository extends JpaRepository<UsuarioPermissao, Long> {

    // ⚠️ Queries com tenantGroupId null precisam usar IS NULL em SQL
    // Suporta tenants com e sem particionamento por grupos
    @Query("""
            SELECT CASE WHEN COUNT(up) > 0 THEN true ELSE false END
            FROM UsuarioPermissao up
            WHERE up.usuario.id = :usuarioId
              AND up.permissao.id = :permissaoId
              AND up.tenantId = :tenantId
              AND (
                (:tenantGroupId IS NULL AND up.tenantGroupId IS NULL)
                OR up.tenantGroupId = :tenantGroupId
              )
            """)
    boolean existsByUsuarioIdAndPermissaoIdAndTenantIdAndTenantGroupId(
            @Param("usuarioId") Long usuarioId,
            @Param("permissaoId") Long permissaoId,
            @Param("tenantId") Long tenantId,
            @Param("tenantGroupId") Long tenantGroupId
    );
}

