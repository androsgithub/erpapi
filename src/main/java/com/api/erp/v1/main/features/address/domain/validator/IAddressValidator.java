package com.api.erp.v1.main.features.address.domain.validator;

import com.api.erp.v1.main.features.address.application.dto.request.CreateAddressRequest;
import org.springframework.data.domain.Pageable;

public interface IAddressValidator {
    void validarCriacao(CreateAddressRequest dto);
    void validarAtualizacao(Long id, CreateAddressRequest dto);
    void validarPageable(Pageable pageable);
    void validarDTO(CreateAddressRequest dto);
    void validarId(Long id);
}
