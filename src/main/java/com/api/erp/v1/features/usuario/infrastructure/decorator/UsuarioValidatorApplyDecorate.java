package com.api.erp.v1.features.usuario.infrastructure.decorator;

import com.api.erp.v1.tenant.domain.entity.UsuarioConfig;
import com.api.erp.v1.features.usuario.domain.validator.IUsuarioValidator;

public class UsuarioValidatorApplyDecorate {
    public static IUsuarioValidator aplicarDecorators(IUsuarioValidator validator, UsuarioConfig config) {
        if(config.isUsuarioCorporateEmailRequired()){
            validator = new EmailCorporativoValidatorDecorator(validator, config.getAllowedEmailDomains());
        }
        return validator;
    }
}
