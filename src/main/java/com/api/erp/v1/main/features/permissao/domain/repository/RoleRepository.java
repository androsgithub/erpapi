package com.api.erp.v1.main.features.permissao.domain.repository;

import com.api.erp.v1.main.features.permissao.domain.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByNome(String nome);
}
