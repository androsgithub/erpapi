package com.api.erp.v1.features.usuario.infrastructure.factory;

import com.api.erp.v1.features.empresa.domain.entity.UsuarioConfig;
import com.api.erp.v1.features.empresa.domain.service.IEmpresaService;
import com.api.erp.v1.features.permissao.domain.repository.PermissaoRepository;
import com.api.erp.v1.features.permissao.domain.repository.RoleRepository;
import com.api.erp.v1.features.permissao.domain.repository.UsuarioPermissaoRepository;
import com.api.erp.v1.features.usuario.domain.repository.UsuarioRepository;
import com.api.erp.v1.features.usuario.domain.service.INotificacaoService;
import com.api.erp.v1.features.usuario.domain.service.IPasswordEncoder;
import com.api.erp.v1.features.usuario.domain.service.IUsuarioService;
import com.api.erp.v1.features.usuario.domain.validator.IUsuarioValidator;
import com.api.erp.v1.features.usuario.infrastructure.decorator.GestorAprovacaoServiceDecorator;
import com.api.erp.v1.features.usuario.infrastructure.service.UsuarioService;
import org.springframework.stereotype.Component;

@Component
public class UsuarioServiceFactory {
    private final UsuarioRepository repository;
    private final IPasswordEncoder passwordEncoder;
    private final INotificacaoService notificacaoService;
    private final IEmpresaService empresaService;

    private final PermissaoRepository permissaoRepository;
    private final RoleRepository roleRepository;
    private final UsuarioPermissaoRepository usuarioPermissaoRepository;

    public UsuarioServiceFactory(UsuarioRepository repository, IPasswordEncoder passwordEncoder, INotificacaoService notificacaoService, IEmpresaService empresaService, PermissaoRepository permissaoRepository, RoleRepository roleRepository, UsuarioPermissaoRepository usuarioPermissaoRepository) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.notificacaoService = notificacaoService;
        this.empresaService = empresaService;
        this.permissaoRepository = permissaoRepository;
        this.roleRepository = roleRepository;
        this.usuarioPermissaoRepository = usuarioPermissaoRepository;
    }


    public IUsuarioService create() {
        UsuarioConfig usuarioConfig = empresaService.getEmpresaConfig().getUsuarioConfig();
        // Cria validator base
        IUsuarioValidator validator = new UsuarioValidatorFactory().create(usuarioConfig);


        // Cria serviço base
        IUsuarioService service = new UsuarioService(repository, validator, passwordEncoder, permissaoRepository, roleRepository, usuarioPermissaoRepository);

        // Adiciona decorator de aprovação de gestor se necessário
        if (usuarioConfig != null && usuarioConfig.isUsuarioApprovalRequired()) {
            service = new GestorAprovacaoServiceDecorator(service, notificacaoService);
        }

        return service;
    }
}
