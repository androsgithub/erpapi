package com.api.erp.features.usuario.infrastructure.factory;

import com.api.erp.features.usuario.application.validator.BasicUsuarioValidator;
import com.api.erp.features.empresa.domain.factory.EmpresaConfig;
import com.api.erp.features.usuario.domain.validator.UsuarioValidator;
import com.api.erp.features.usuario.infrastructure.decorator.EmailCorporativoValidatorDecorator;
import org.springframework.stereotype.Component;

@Component
public class UsuarioValidatorFactory {
    
    public UsuarioValidator create(EmpresaConfig config) {
        // Sempre começa com validador básico
        UsuarioValidator validator = new BasicUsuarioValidator();
        
        // Adiciona decorator de email corporativo se necessário
        if (config.isRequerEmailCorporativo()) {
            validator = new EmailCorporativoValidatorDecorator(
                validator, 
                config.getDominiosPermitidos()
            );
        }
        
        // Aqui podem ser adicionados outros decorators conforme necessário
        
        return validator;
    }
}
