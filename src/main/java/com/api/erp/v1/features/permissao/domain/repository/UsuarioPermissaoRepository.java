package com.api.erp.v1.features.permissao.domain.repository;

import com.api.erp.v1.features.permissao.domain.entity.UsuarioPermissao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioPermissaoRepository extends JpaRepository<UsuarioPermissao, Long> {
    Optional<UsuarioPermissao> findByUsuarioId(Long usuarioId);

    @Query(value = """
            SELECT COUNT(*)
            FROM usuario_permissao up
            LEFT JOIN usuario_permissao_direta upd 
                ON upd.usuario_permissao_id = up.id
            LEFT JOIN permissao p1 
                ON p1.id = upd.permissao_id
            LEFT JOIN usuario_role ur 
                ON ur.usuario_permissao_id = up.id
            LEFT JOIN role_permissao rp 
                ON rp.role_id = ur.role_id
            LEFT JOIN permissao p2 
                ON p2.id = rp.permissao_id
            WHERE up.usuario_id = :usuarioId
              AND (p1.codigo = :permissaoCodigo OR p2.codigo = :permissaoCodigo)
            """, nativeQuery = true)
    long countPermissao(
            @Param("usuarioId") Long usuarioId,
            @Param("permissaoCodigo") String permissaoCodigo
    );
}
