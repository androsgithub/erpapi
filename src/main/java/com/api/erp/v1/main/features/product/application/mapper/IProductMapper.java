package com.api.erp.v1.main.features.product.application.mapper;

import com.api.erp.v1.main.features.product.application.dto.ProductResponseDTO;
import com.api.erp.v1.main.features.product.domain.entity.Product;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface IProductMapper {

    ProductResponseDTO toResponse(Product product);

    List<ProductResponseDTO> toResponseList(List<Product> products);

    default Page<ProductResponseDTO> toResponsePage(Page<Product> page) {
        return page.map(this::toResponse);
    }
}


