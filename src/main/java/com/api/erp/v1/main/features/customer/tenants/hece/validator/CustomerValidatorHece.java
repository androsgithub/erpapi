package com.api.erp.v1.main.features.customer.tenants.hece.validator;

import com.api.erp.v1.main.features.customer.application.dto.request.CreateCustomerDto;
import com.api.erp.v1.main.features.customer.domain.validator.ICustomerValidator;
import com.api.erp.v1.main.shared.domain.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class CustomerValidatorHece implements ICustomerValidator {

    @Qualifier("customerValidatorDefault")
    private final ICustomerValidator defaultValidator;

    @Autowired
    public CustomerValidatorHece(ICustomerValidator defaultValidator) {
        this.defaultValidator = defaultValidator;
    }

    @Override
    public void validarCriacao(CreateCustomerDto dto) {
        // 1. Validações padrão
        defaultValidator.validarCriacao(dto);

//        String emailInterno = dto.customData().getOptional("email_interno", String.class).orElseThrow(() -> new BusinessException("Deve ser informado o e-mail interno"));

        // 2. Validações específicas Empresa A
//        validarEmailInterno(emailInterno);
    }

    @Override
    public void validarAtualizacao(Long id, CreateCustomerDto dto) {
        defaultValidator.validarAtualizacao(id, dto);
//        String emailInterno = dto.customData().getOptional("email_interno", String.class).orElseThrow(() -> new BusinessException("Deve ser informado o e-mail interno"));
//        validarEmailInterno(emailInterno);
    }

    @Override
    public void validarPageable(Pageable pageable) {
        defaultValidator.validarPageable(pageable);
    }

    @Override
    public void validarDTO(CreateCustomerDto customerDto) {
        defaultValidator.validarDTO(customerDto);
    }

    @Override
    public void validarId(Long id) {
        defaultValidator.validarId(id);
    }

    private void validarEmailInterno(String emailInterno) {
        if (emailInterno == null || emailInterno.isBlank()) {
            throw new BusinessException(
                    "[HECE] Email interno é obrigatório");
        }

        if (!emailInterno.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new BusinessException(
                    "[HECE] Email interno inválido");
        }

        // Validação extra: domínio corporativo
        if (!emailInterno.endsWith("@hece.com")) {
            throw new BusinessException(
                    "[HECE] Email deve ser do domínio @hece.com");
        }
    }
}
