package com.api.erp.v1.main.features.usuario.infrastructure.decorator;

import com.api.erp.v1.main.tenant.domain.entity.configs.UsuarioConfig;
import com.api.erp.v1.main.features.usuario.domain.validator.IUsuarioValidator;

public class UsuarioValidatorApplyDecorate {
    public static IUsuarioValidator aplicarDecorators(IUsuarioValidator validator, UsuarioConfig config) {
        if(config.isUsuarioCorporateEmailRequired()){
            validator = new EmailCorporativoValidatorDecorator(validator, config.getAllowedEmailDomains());
        }
        return validator;
    }
}
