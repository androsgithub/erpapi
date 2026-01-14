package com.api.erp.v1.features.endereco.infrastructure.proxy;

import com.api.erp.v1.features.endereco.application.dto.request.CreateEnderecoRequest;
import com.api.erp.v1.features.endereco.domain.validator.IEnderecoValidator;
import com.api.erp.v1.shared.infrastructure.security.annotations.RequiresPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EnderecoValidatorProxy {

    private final IEnderecoValidator validator;

    public void validarCriacao(CreateEnderecoRequest dto) {
        validator.validarCriacao(dto);
    }

    public void validarAtualizacao(Long id, CreateEnderecoRequest dto) {
        validator.validarAtualizacao(id, dto);
    }

    public void validarPageable(Pageable pageable) {
        validator.validarPageable(pageable);
    }

    public void validarDTO(CreateEnderecoRequest dto) {
        validator.validarDTO(dto);
    }

    public void validarId(Long id) {
        validator.validarId(id);
    }
}
