package com.api.erp.v1.features.usuario.infrastructure.decorator;

import com.api.erp.v1.tenant.domain.entity.UsuarioConfig;
import com.api.erp.v1.features.usuario.domain.service.IUsuarioService;

public class UsuarioServiceApplyDecorate {
    public static IUsuarioService aplicarDecorators(IUsuarioService service, UsuarioConfig config) {
        if(config.isUsuarioApprovalRequired()){
            service = new GestorAprovacaoServiceDecorator(service);
        }
        return service;
    }
}
