package com.api.erp.v1.features.cliente.domain.service;

import com.api.erp.v1.features.cliente.application.dto.request.CreateClienteDto;
import com.api.erp.v1.features.cliente.domain.entity.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IClienteService {
    public Page<Cliente> pegarTodos(Pageable pageable);
    public Cliente criar(CreateClienteDto clienteDto);
    public Cliente atualizar(Long id, CreateClienteDto clienteDto);

    public Cliente pegarPorId(Long id);

    public void deletar(Long id);
}
