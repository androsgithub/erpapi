package com.api.erp.v1.main.features.usuario.domain.repository;

import com.api.erp.v1.main.features.usuario.domain.entity.StatusUsuario;
import com.api.erp.v1.main.features.usuario.domain.entity.Usuario;
import com.api.erp.v1.main.shared.domain.valueobject.CPF;
import com.api.erp.v1.main.shared.domain.valueobject.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Usuario save(Usuario usuario);

    Optional<Usuario> findById(Long id);

    Optional<Usuario> findByEmail(Email email);

    Optional<Usuario> findByCpf(CPF cpf);

    List<Usuario> findAll();

    List<Usuario> findByStatus(StatusUsuario status);

    boolean existsByEmail(Email email);

    boolean existsByCpf(CPF cpf);

    void deleteById(UUID id);

}
