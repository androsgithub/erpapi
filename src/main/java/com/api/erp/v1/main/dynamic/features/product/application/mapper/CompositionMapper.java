package com.api.erp.v1.main.dynamic.features.product.application.mapper;

import com.api.erp.v1.main.dynamic.features.product.application.dto.CompositionResponseDTO;
import com.api.erp.v1.main.dynamic.features.product.domain.entity.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CompositionMapper {

    CompositionResponseDTO toResponse(Product product);
}
