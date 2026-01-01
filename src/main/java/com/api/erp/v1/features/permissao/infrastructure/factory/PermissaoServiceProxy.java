package com.api.erp.v1.features.permissao.infrastructure.factory;

import com.api.erp.v1.features.permissao.domain.service.IPermissaoService;
import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * Proxy de PermissaoService que delega para PermissaoServiceHolder.
 */
@RequiredArgsConstructor
public class PermissaoServiceProxy implements IPermissaoService {

    private final PermissaoServiceHolder holder;

    @Override
    public boolean hasPermission(Long usuarioId, String permissaoCodigo, Map<String, String> contexto) {
        return holder.getService().hasPermission(usuarioId, permissaoCodigo, contexto);
    }
}
