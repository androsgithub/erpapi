package com.api.erp.v1.main.features.cliente.domain.repository;

import com.api.erp.v1.main.features.cliente.domain.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
