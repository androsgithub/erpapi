package com.api.erp.features.usuario.domain.repository;

import com.api.erp.features.usuario.domain.entity.Usuario;
import com.api.erp.features.usuario.domain.entity.StatusUsuario;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository {
    Usuario save(Usuario usuario);
    Optional<Usuario> findById(UUID id);
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByCpf(String cpf);
    List<Usuario> findAll();
    List<Usuario> findByStatus(StatusUsuario status);
    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);
    void deleteById(UUID id);
}
