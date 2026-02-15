package com.api.erp.v1.main.features.endereco.domain.validator;

import com.api.erp.v1.main.features.endereco.application.dto.request.CreateEnderecoRequest;
import org.springframework.data.domain.Pageable;

public interface IEnderecoValidator {
    void validarCriacao(CreateEnderecoRequest dto);
    void validarAtualizacao(Long id, CreateEnderecoRequest dto);
    void validarPageable(Pageable pageable);
    void validarDTO(CreateEnderecoRequest dto);
    void validarId(Long id);
}
