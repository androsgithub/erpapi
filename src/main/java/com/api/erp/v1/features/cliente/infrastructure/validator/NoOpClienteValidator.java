package com.api.erp.v1.features.cliente.infrastructure.validator;

import com.api.erp.v1.features.cliente.application.dto.request.CreateClienteDto;
import com.api.erp.v1.features.cliente.domain.validator.IClienteValidator;
import com.api.erp.v1.shared.domain.enums.TenantCode;
import com.api.erp.v1.shared.domain.enums.TenantType;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class NoOpClienteValidator implements IClienteValidator {
    @Override
    public void validarCriacao(CreateClienteDto dto) {

    }

    @Override
    public void validarAtualizacao(Long id, CreateClienteDto dto) {

    }

    @Override
    public void validarPageable(Pageable pageable) {

    }

    @Override
    public void validarDTO(CreateClienteDto clienteDto) {

    }

    @Override
    public void validarId(Long id) {

    }
}
