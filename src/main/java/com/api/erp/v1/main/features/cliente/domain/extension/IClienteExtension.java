package com.api.erp.v1.main.features.cliente.domain.extension;

import com.api.erp.v1.main.features.cliente.application.dto.request.CreateClienteDto;
import com.api.erp.v1.main.features.cliente.domain.entity.Cliente;

public interface IClienteExtension {
    void antesDeCriar(Cliente cliente, CreateClienteDto dto);

    void depoisDeCriar(Cliente cliente, CreateClienteDto dto);

    void antesDeAtualizar(Cliente cliente, CreateClienteDto dto);

    void depoisDeAtualizar(Cliente cliente, CreateClienteDto dto);

    void antesDeDeletar(Cliente cliente);

    void depoisDeDeletar(Cliente cliente);

    String getTenantCode();

    boolean isEnabled();
}
