package com.api.erp.v1.features.endereco.infrastructure.validator;


import com.api.erp.v1.features.endereco.application.dto.request.CreateEnderecoRequest;
import com.api.erp.v1.features.endereco.domain.validator.IEnderecoValidator;
import org.springframework.data.domain.Pageable;

public class NoOpEnderecoValidator implements IEnderecoValidator {

    @Override
    public void validarCriacao(CreateEnderecoRequest dto) {
        // No-op: sem validação
    }

    @Override
    public void validarAtualizacao(Long id, CreateEnderecoRequest dto) {
        // No-op: sem validação
    }

    @Override
    public void validarPageable(Pageable pageable) {
        // No-op: sem validação
    }

    @Override
    public void validarDTO(CreateEnderecoRequest dto) {
        // No-op: sem validação
    }

    @Override
    public void validarId(Long id) {
        // No-op: sem validação
    }
}
