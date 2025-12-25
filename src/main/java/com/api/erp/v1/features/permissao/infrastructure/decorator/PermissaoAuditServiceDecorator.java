package com.api.erp.v1.features.permissao.infrastructure.decorator;

import com.api.erp.v1.features.permissao.domain.service.PermissaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class PermissaoAuditServiceDecorator implements PermissaoService {

    private final PermissaoService delegate;

    @Override
    public boolean hasPermission(Long usuarioId, String permissaoCodigo, Map<String, String> contexto) {
        log.info("Audit: Verificando acesso para usuário {} na permissão '{}' com contexto {}",
                usuarioId, permissaoCodigo, contexto);

        boolean result = delegate.hasPermission(usuarioId, permissaoCodigo, contexto);

        if (result) {
            log.info("Audit: Acesso CONCEDIDO para usuário {} na permissão '{}'", usuarioId, permissaoCodigo);
        } else {
            log.warn("Audit: Acesso NEGADO para usuário {} na permissão '{}'", usuarioId, permissaoCodigo);
        }
        return result;
    }
}
