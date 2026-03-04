package com.api.erp.v1.main.features.address.infrastructure.proxy;

import com.api.erp.v1.main.features.address.application.dto.request.CreateAddressRequest;
import com.api.erp.v1.main.features.address.domain.validator.IAddressValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddressValidatorProxy {

    private final IAddressValidator validator;

    public void validarCriacao(CreateAddressRequest dto) {
        validator.validarCriacao(dto);
    }

    public void validarAtualizacao(Long id, CreateAddressRequest dto) {
        validator.validarAtualizacao(id, dto);
    }

    public void validarPageable(Pageable pageable) {
        validator.validarPageable(pageable);
    }

    public void validarDTO(CreateAddressRequest dto) {
        validator.validarDTO(dto);
    }

    public void validarId(Long id) {
        validator.validarId(id);
    }
}
