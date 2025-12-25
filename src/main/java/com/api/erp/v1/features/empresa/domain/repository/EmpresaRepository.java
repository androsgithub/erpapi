package com.api.erp.v1.features.empresa.domain.repository;

import com.api.erp.v1.features.empresa.domain.entity.Empresa;
import com.api.erp.v1.features.permissao.domain.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    Optional<Role> findByNome(String nome);
}
