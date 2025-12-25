package com.api.erp.v1.features.endereco.domain.repository;

import com.api.erp.v1.features.endereco.domain.entity.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Long> {
}
