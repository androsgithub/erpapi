package com.api.erp.v1.shared.infrastructure.config.startup.seed;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class MainSeed {

    private final PermissaoSeed permissaoSeed;
    private final UsuarioAdminSeed usuarioAdminSeed;
    private final UnidadeMedidaSeed unidadeMedidaSeed;

    @Transactional
    public void executar() {
        permissaoSeed.executar();
        usuarioAdminSeed.executar();
//        unidadeMedidaSeed.executar();
    }
}

