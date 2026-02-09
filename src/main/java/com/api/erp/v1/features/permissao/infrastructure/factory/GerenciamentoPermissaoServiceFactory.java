package com.api.erp.v1.features.permissao.infrastructure.factory;

import com.api.erp.v1.features.permissao.domain.repository.PermissaoRepository;
import com.api.erp.v1.features.permissao.domain.repository.RoleRepository;
import com.api.erp.v1.features.permissao.domain.service.IGerenciamentoPermissaoService;
import com.api.erp.v1.features.permissao.infrastructure.service.GerenciamentoPermissaoService;
import com.api.erp.v1.features.usuario.domain.repository.UsuarioRepository;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GerenciamentoPermissaoServiceFactory {
    private final PermissaoRepository permissaoRepository;
    private final RoleRepository roleRepository;
    private final UsuarioRepository usuarioRepository;

    public GerenciamentoPermissaoServiceFactory(PermissaoRepository permissaoRepository, RoleRepository roleRepository, UsuarioRepository usuarioRepository) {
        this.permissaoRepository = permissaoRepository;
        this.roleRepository = roleRepository;
        this.usuarioRepository = usuarioRepository;
    }


    public IGerenciamentoPermissaoService create() {
        IGerenciamentoPermissaoService service = new GerenciamentoPermissaoService(permissaoRepository, roleRepository, usuarioRepository);
        return service;
    }
}
