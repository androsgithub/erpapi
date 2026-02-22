package com.api.erp.v1.main.features.product.infrastructure.decorator;

import com.api.erp.v1.main.tenant.domain.entity.configs.ProductConfig;
import com.api.erp.v1.main.features.product.domain.service.IListaExpandidaProducaoService;

public class ListaExpandidaProducaoServiceApplyDecorate {
    public static IListaExpandidaProducaoService aplicarDecorators(IListaExpandidaProducaoService service, ProductConfig config) {
        return service;
    }
}
