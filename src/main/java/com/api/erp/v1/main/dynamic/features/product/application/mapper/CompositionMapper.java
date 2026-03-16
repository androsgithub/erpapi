package com.api.erp.v1.main.dynamic.features.product.application.mapper;

import com.api.erp.v1.main.dynamic.features.product.application.dto.CompositionResponseDTO;
import com.api.erp.v1.main.dynamic.features.product.domain.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class CompositionMapper {

    public CompositionResponseDTO toResponse(Product product) {
        if (product == null) {
            return null;
        }
        // Since Product is not a composition entity, return null or an empty DTO
        return null;
    }
}
