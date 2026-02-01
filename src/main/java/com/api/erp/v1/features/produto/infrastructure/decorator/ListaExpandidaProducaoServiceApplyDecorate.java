package com.api.erp.v1.features.produto.infrastructure.decorator;

import com.api.erp.v1.tenant.domain.entity.configs.ProdutoConfig;
import com.api.erp.v1.features.produto.domain.service.IListaExpandidaProducaoService;

public class ListaExpandidaProducaoServiceApplyDecorate {
    public static IListaExpandidaProducaoService aplicarDecorators(IListaExpandidaProducaoService service, ProdutoConfig config) {
        return service;
    }
}
