package com.api.erp.v1.features.produto.infrastructure.decorator;

import com.api.erp.v1.tenant.domain.entity.ProdutoConfig;
import com.api.erp.v1.features.produto.domain.service.IListaExpandidaService;

public class ListaExpandidaServiceApplyDecorate {
    public static IListaExpandidaService aplicarDecorators(IListaExpandidaService service, ProdutoConfig config) {
        return service;
    }
}
