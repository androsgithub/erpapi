package com.api.erp.v1.main.features.user.infrastructure.decorator;

import com.api.erp.v1.main.tenant.domain.entity.configs.UserConfig;
import com.api.erp.v1.main.features.user.domain.validator.IUserValidator;

public class UserValidatorApplyDecorate {
    public static IUserValidator aplicarDecorators(IUserValidator validator, UserConfig config) {
        if(config.isUserCorporateEmailRequired()){
            validator = new EmailCorporativoValidatorDecorator(validator, config.getAllowedEmailDomains());
        }
        return validator;
    }
}
