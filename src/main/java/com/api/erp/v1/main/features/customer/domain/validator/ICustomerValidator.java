package com.api.erp.v1.main.features.customer.domain.validator;

import com.api.erp.v1.main.features.customer.application.dto.request.CreateCustomerDto;
import org.springframework.data.domain.Pageable;

public interface ICustomerValidator {
    void validarCriacao(CreateCustomerDto dto);

    void validarAtualizacao(Long id, CreateCustomerDto dto);

    void validarPageable(Pageable pageable);

    void validarDTO(CreateCustomerDto customerDto);

    void validarId(Long id);
}
