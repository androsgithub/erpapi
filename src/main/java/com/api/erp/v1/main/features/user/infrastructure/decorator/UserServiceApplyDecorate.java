package com.api.erp.v1.main.features.user.infrastructure.decorator;

import com.api.erp.v1.main.tenant.domain.entity.configs.UserConfig;
import com.api.erp.v1.main.features.user.domain.service.IUserService;

public class UserServiceApplyDecorate {
    public static IUserService aplicarDecorators(IUserService service, UserConfig config) {
        if(config.isUserApprovalRequired()){
            service = new GestorAprovacaoServiceDecorator(service);
        }
        return service;
    }
}
