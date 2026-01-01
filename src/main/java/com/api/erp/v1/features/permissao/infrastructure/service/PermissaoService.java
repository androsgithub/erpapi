package com.api.erp.v1.features.permissao.infrastructure.service;

import com.api.erp.v1.features.permissao.domain.repository.UsuarioPermissaoRepository;
import com.api.erp.v1.features.permissao.domain.service.IPermissaoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
public class PermissaoService implements IPermissaoService {

    private final UsuarioPermissaoRepository usuarioPermissaoRepository;

    public PermissaoService(UsuarioPermissaoRepository usuarioPermissaoRepository) {
        this.usuarioPermissaoRepository = usuarioPermissaoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasPermission(Long usuarioId, String permissaoCodigo, Map<String, String> contexto) {
        return usuarioPermissaoRepository.countPermissao(usuarioId, permissaoCodigo) > 0;
    }

}
