package com.api.erp.v1.features.usuario.infrastructure.factory;

import com.api.erp.v1.features.empresa.domain.entity.UsuarioConfig;
import com.api.erp.v1.features.usuario.application.validator.BasicUsuarioValidator;
import com.api.erp.v1.features.usuario.domain.validator.IUsuarioValidator;
import com.api.erp.v1.features.usuario.infrastructure.decorator.EmailCorporativoValidatorDecorator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsuarioValidatorFactory {

    public IUsuarioValidator create(UsuarioConfig config) {
        // Sempre começa com validador básico
        IUsuarioValidator validator = new BasicUsuarioValidator();

        // Adiciona decorator de email corporativo se necessário
        if (config.isUsuarioCorporateEmailRequired()) {
            validator = new EmailCorporativoValidatorDecorator(
                    validator,
                    config.getAllowedEmailDomains()
            );
        }

        // Aqui podem ser adicionados outros decorators conforme necessário

        return validator;
    }
}
