package com.api.erp.v1.main.features.cliente.domain.validator;

import com.api.erp.v1.main.features.cliente.application.dto.request.CreateClienteDto;
import org.springframework.data.domain.Pageable;

public interface IClienteValidator {
    void validarCriacao(CreateClienteDto dto);

    void validarAtualizacao(Long id, CreateClienteDto dto);

    void validarPageable(Pageable pageable);

    void validarDTO(CreateClienteDto clienteDto);

    void validarId(Long id);
}
