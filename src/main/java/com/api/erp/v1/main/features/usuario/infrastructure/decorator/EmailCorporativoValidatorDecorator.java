package com.api.erp.v1.main.features.usuario.infrastructure.decorator;

import com.api.erp.v1.main.features.usuario.application.dto.request.CreateUsuarioRequest;
import com.api.erp.v1.main.features.usuario.domain.validator.IUsuarioValidator;
import com.api.erp.v1.main.shared.domain.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class EmailCorporativoValidatorDecorator implements IUsuarioValidator {
    private final IUsuarioValidator wrapped;
    private final List<String> dominiosPermitidos;

    public EmailCorporativoValidatorDecorator(IUsuarioValidator wrapped, List<String> dominiosPermitidos) {
        this.wrapped = wrapped;
        this.dominiosPermitidos = dominiosPermitidos;
    }

    @Override
    public void validar(CreateUsuarioRequest request) {
        // Executa validação base
        wrapped.validar(request);

        // Adiciona validação de domínio corporativo
        String email = request.email();
        String dominio = email.substring(email.indexOf('@') + 1);

        boolean dominioValido = dominiosPermitidos.stream()
                .anyMatch(d -> dominio.equalsIgnoreCase(d));

        log.info(dominio + " " + Boolean.toString(dominioValido));

        if (!dominioValido) {
            throw new BusinessException("Email deve ser de domínio corporativo: " + dominiosPermitidos);
        }
    }
}
