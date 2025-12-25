package com.api.erp.v1.features.permissao.domain.repository;

import com.api.erp.v1.features.permissao.domain.entity.UsuarioPermissao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioPermissaoRepository extends JpaRepository<UsuarioPermissao, Long> {
    Optional<UsuarioPermissao> findByUsuarioId(Long usuarioId);
}
