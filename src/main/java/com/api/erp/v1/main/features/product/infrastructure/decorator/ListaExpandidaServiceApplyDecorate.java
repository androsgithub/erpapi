package com.api.erp.v1.main.features.product.infrastructure.decorator;

import com.api.erp.v1.main.tenant.domain.entity.configs.ProductConfig;
import com.api.erp.v1.main.features.product.domain.service.IListaExpandidaService;

public class ListaExpandidaServiceApplyDecorate {
    public static IListaExpandidaService aplicarDecorators(IListaExpandidaService service, ProductConfig config) {
        return service;
    }
}
