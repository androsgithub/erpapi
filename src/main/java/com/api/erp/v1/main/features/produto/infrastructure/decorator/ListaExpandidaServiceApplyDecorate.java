package com.api.erp.v1.main.features.produto.infrastructure.decorator;

import com.api.erp.v1.main.tenant.domain.entity.configs.ProdutoConfig;
import com.api.erp.v1.main.features.produto.domain.service.IListaExpandidaService;

public class ListaExpandidaServiceApplyDecorate {
    public static IListaExpandidaService aplicarDecorators(IListaExpandidaService service, ProdutoConfig config) {
        return service;
    }
}
