package com.api.erp.v1.main.features.product.infrastructure.decorator;

import com.api.erp.v1.main.tenant.domain.entity.configs.ProductConfig;
import com.api.erp.v1.main.features.product.domain.service.IProductService;

public class ProductServiceApplyDecorate {
    public static IProductService aplicarDecorators(IProductService service, ProductConfig config) {
        return service;
    }
}
