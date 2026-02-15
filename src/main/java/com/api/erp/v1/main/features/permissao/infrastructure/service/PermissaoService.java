package com.api.erp.v1.main.features.permissao.infrastructure.service;

import com.api.erp.v1.main.features.permissao.domain.repository.PermissaoRepository;
import com.api.erp.v1.main.features.permissao.domain.service.IPermissaoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
public class PermissaoService implements IPermissaoService {

    private final PermissaoRepository permissaoRepository;

    public PermissaoService(PermissaoRepository usuarioPermissaoRepository) {
        this.permissaoRepository = usuarioPermissaoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasPermission(Long usuarioId, String permissaoCodigo, Map<String, String> contexto) {
        return permissaoRepository.countPermissao(usuarioId, permissaoCodigo) > 0;
    }

}
