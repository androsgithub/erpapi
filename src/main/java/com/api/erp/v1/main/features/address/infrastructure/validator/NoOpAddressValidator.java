package com.api.erp.v1.main.features.address.infrastructure.validator;


import com.api.erp.v1.main.features.address.application.dto.request.CreateAddressRequest;
import com.api.erp.v1.main.features.address.domain.validator.IAddressValidator;
import org.springframework.data.domain.Pageable;

public class NoOpAddressValidator implements IAddressValidator {

    @Override
    public void validarCriacao(CreateAddressRequest dto) {
        // No-op: sem validação
    }

    @Override
    public void validarAtualizacao(Long id, CreateAddressRequest dto) {
        // No-op: sem validação
    }

    @Override
    public void validarPageable(Pageable pageable) {
        // No-op: sem validação
    }

    @Override
    public void validarDTO(CreateAddressRequest dto) {
        // No-op: sem validação
    }

    @Override
    public void validarId(Long id) {
        // No-op: sem validação
    }
}
