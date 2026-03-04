package com.api.erp.v1.main.features.user.infrastructure.decorator;

import com.api.erp.v1.main.features.user.application.dto.request.CreateUserRequest;
import com.api.erp.v1.main.features.user.domain.validator.IUserValidator;
import com.api.erp.v1.main.shared.domain.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class EmailCorporativoValidatorDecorator implements IUserValidator {
    private final IUserValidator wrapped;
    private final List<String> dominiosPermitidos;

    public EmailCorporativoValidatorDecorator(IUserValidator wrapped, List<String> dominiosPermitidos) {
        this.wrapped = wrapped;
        this.dominiosPermitidos = dominiosPermitidos;
    }

    @Override
    public void validar(CreateUserRequest request) {
        // Executes validação base
        wrapped.validar(request);

        // Adiciona validação de domínio corporativo
        String email = request.email();
        String dominio = email.substring(email.indexOf('@') + 1);

        boolean dominioValido = dominiosPermitidos.stream()
                .anyMatch(d -> dominio.equalsIgnoreCase(d));

        log.info(dominio + " " + Boolean.toString(dominioValido));

        if (!dominioValido) {
            throw new BusinessException("Email must be from corporate domain: " + dominiosPermitidos);
        }
    }
}
