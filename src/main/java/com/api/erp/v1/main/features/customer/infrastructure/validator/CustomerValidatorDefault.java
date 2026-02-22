package com.api.erp.v1.main.features.customer.infrastructure.validator;

import com.api.erp.v1.main.features.customer.application.dto.request.CreateCustomerDto;
import com.api.erp.v1.main.features.customer.domain.validator.ICustomerValidator;
import com.api.erp.v1.main.shared.domain.exception.BusinessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class CustomerValidatorDefault implements ICustomerValidator {

    @Override
    public void validarCriacao(CreateCustomerDto dto) {
        if (dto.nome() == null || dto.nome().isBlank()) {
            throw new IllegalArgumentException("Customer name is required");
        }

        if (dto.nome().length() < 3) {
            throw new IllegalArgumentException("Name must have at least 3 characters");
        }
    }

    @Override
    public void validarAtualizacao(Long id, CreateCustomerDto dto) {
        if (id == null) {
            throw new IllegalArgumentException("Customer ID is required");
        }
        validarCriacao(dto);
    }

    @Override
    public void validarPageable(Pageable pageable) {
        if (pageable == null) {
            throw new BusinessException("Pagination configuration for customer not found");
        }
    }

    @Override
    public void validarDTO(CreateCustomerDto customerDto) {
        if (customerDto == null) {
            throw new BusinessException("CreateCustomerDto cannot be null");
        }
        if (customerDto.nome() == null || customerDto.nome().isBlank()) {
            throw new BusinessException("Customer name is required");
        }
        if (customerDto.dadosFiscais() == null) {
            throw new BusinessException("Tax data is required");
        }

        // Validações de dados fiscais
        var df = customerDto.dadosFiscais();
        if (df.cnpj() == null || df.cnpj().isBlank()) {
            throw new BusinessException("CNPJ é obrigatório");
        }
        if (df.razaoSocial() == null || df.razaoSocial().isBlank()) {
            throw new BusinessException("Razão social é obrigatória");
        }
    }

    @Override
    public void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException("ID do customer inválido");
        }
    }
}
