package com.api.erp.v1.main.features.customer.infrastructure.validator;

import com.api.erp.v1.main.features.customer.application.dto.request.CreateCustomerDto;
import com.api.erp.v1.main.features.customer.domain.validator.ICustomerValidator;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class NoOpCustomerValidator implements ICustomerValidator {
    @Override
    public void validarCriacao(CreateCustomerDto dto) {
        // No operation
    }

    @Override
    public void validarAtualizacao(Long id, CreateCustomerDto dto) {
        // No operation
    }

    @Override
    public void validarPageable(Pageable pageable) {
        // No operation
    }

    @Override
    public void validarDTO(CreateCustomerDto customerDto) {
        // No operation
    }

    @Override
    public void validarId(Long id) {
        // No operation
    }
}
