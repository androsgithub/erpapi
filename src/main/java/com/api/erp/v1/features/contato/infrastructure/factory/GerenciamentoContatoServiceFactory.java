package com.api.erp.v1.features.contato.infrastructure.factory;

import com.api.erp.v1.features.contato.domain.repository.ContatoRepository;
import com.api.erp.v1.features.contato.domain.repository.UsuarioContatoRepository;
import com.api.erp.v1.features.contato.domain.service.IGerenciamentoContatoService;
import com.api.erp.v1.features.contato.infrastructure.service.GerenciamentoContatoService;
import com.api.erp.v1.features.empresa.domain.entity.ContatoConfig;
import com.api.erp.v1.features.empresa.domain.service.IEmpresaService;
import com.api.erp.v1.features.usuario.domain.repository.UsuarioRepository;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GerenciamentoContatoServiceFactory {

    private final UsuarioContatoRepository usuarioContatoRepository;
    private final ContatoRepository contatoRepository;
    private final UsuarioRepository usuarioRepository;

    public GerenciamentoContatoServiceFactory(UsuarioContatoRepository usuarioContatoRepository, ContatoRepository contatoRepository, UsuarioRepository usuarioRepository) {
        this.usuarioContatoRepository = usuarioContatoRepository;
        this.contatoRepository = contatoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public IGerenciamentoContatoService create() {
        IGerenciamentoContatoService service = new GerenciamentoContatoService(usuarioContatoRepository, contatoRepository, usuarioRepository);
        return service;
    }
}
