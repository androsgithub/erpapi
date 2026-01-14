package com.api.erp.v1.features.produto.infrastructure.decorator;

import com.api.erp.v1.features.tenant.domain.entity.ProdutoConfig;
import com.api.erp.v1.features.produto.domain.service.IProdutoService;

public class ProdutoServiceApplyDecorate {
    public static IProdutoService aplicarDecorators(IProdutoService service, ProdutoConfig config) {
        return service;
    }
}
