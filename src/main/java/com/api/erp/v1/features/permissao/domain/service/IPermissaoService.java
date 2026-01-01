package com.api.erp.v1.features.permissao.domain.service;

import java.util.Map;

public interface IPermissaoService {
    boolean hasPermission(Long usuarioId, String permissaoCodigo, Map<String, String> contexto);
}
