package com.api.erp.v1.main.features.cliente.infrastructure.validator;

import com.api.erp.v1.main.features.cliente.application.dto.request.CreateClienteDto;
import com.api.erp.v1.main.features.cliente.domain.validator.IClienteValidator;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class NoOpClienteValidator implements IClienteValidator {
    @Override
    public void validarCriacao(CreateClienteDto dto) {
        // No operation
    }

    @Override
    public void validarAtualizacao(Long id, CreateClienteDto dto) {
        // No operation
    }

    @Override
    public void validarPageable(Pageable pageable) {
        // No operation
    }

    @Override
    public void validarDTO(CreateClienteDto clienteDto) {
        // No operation
    }

    @Override
    public void validarId(Long id) {
        // No operation
    }
}
