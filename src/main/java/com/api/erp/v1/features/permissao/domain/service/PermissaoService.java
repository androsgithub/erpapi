package com.api.erp.v1.features.permissao.domain.service;

import java.util.Map;
import java.util.UUID;

public interface PermissaoService {
    boolean hasPermission(Long usuarioId, String permissaoCodigo, Map<String, String> contexto);
}
