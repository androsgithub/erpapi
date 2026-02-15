package com.api.erp.v1.main.features.produto.infrastructure.decorator;

import com.api.erp.v1.main.tenant.domain.entity.configs.ProdutoConfig;
import com.api.erp.v1.main.features.produto.domain.service.IProdutoService;

public class ProdutoServiceApplyDecorate {
    public static IProdutoService aplicarDecorators(IProdutoService service, ProdutoConfig config) {
        return service;
    }
}
