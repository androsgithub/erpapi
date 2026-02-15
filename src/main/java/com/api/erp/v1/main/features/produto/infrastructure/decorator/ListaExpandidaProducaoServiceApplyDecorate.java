package com.api.erp.v1.main.features.produto.infrastructure.decorator;

import com.api.erp.v1.main.tenant.domain.entity.configs.ProdutoConfig;
import com.api.erp.v1.main.features.produto.domain.service.IListaExpandidaProducaoService;

public class ListaExpandidaProducaoServiceApplyDecorate {
    public static IListaExpandidaProducaoService aplicarDecorators(IListaExpandidaProducaoService service, ProdutoConfig config) {
        return service;
    }
}
