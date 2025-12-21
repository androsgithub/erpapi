package com.api.erp.features.usuario.infrastructure.factory;

import com.api.erp.features.usuario.application.service.UsuarioServiceImpl;
import com.api.erp.features.usuario.application.service.PasswordEncoder;
import com.api.erp.features.usuario.application.validator.BasicUsuarioValidator;
import com.api.erp.features.empresa.domain.factory.EmpresaConfig;
import com.api.erp.features.usuario.domain.repository.UsuarioRepository;
import com.api.erp.features.usuario.domain.service.UsuarioService;
import com.api.erp.features.usuario.domain.validator.UsuarioValidator;
import com.api.erp.features.usuario.infrastructure.decorator.GestorAprovacaoServiceDecorator;
import com.api.erp.features.usuario.infrastructure.decorator.NotificacaoService;
import com.api.erp.features.empresa.application.service.CriarEmpresaService;
import org.springframework.stereotype.Component;

@Component
public class UsuarioServiceFactory {
    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final NotificacaoService notificacaoService;
    private final CriarEmpresaService empresaService;
    
    public UsuarioServiceFactory(
        UsuarioRepository repository,
        PasswordEncoder passwordEncoder,
        NotificacaoService notificacaoService,
        CriarEmpresaService empresaService
    ) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.notificacaoService = notificacaoService;
        this.empresaService = empresaService;
    }
    
    public UsuarioService create() {
        EmpresaConfig config = empresaService.obterConfiguracao();
        // Cria validator base
        UsuarioValidator validator = new UsuarioValidatorFactory().create(config);

        
        // Cria serviço base
        UsuarioService service = new UsuarioServiceImpl(repository, validator, passwordEncoder);
        
        // Adiciona decorator de aprovação de gestor se necessário
        if (config != null && config.isRequerAprovacaoGestor()) {
            service = new GestorAprovacaoServiceDecorator(service, notificacaoService);
        }
        
        return service;
    }
}
