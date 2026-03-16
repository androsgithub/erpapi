package com.api.erp.v1.main.dynamic.features.product.application.mapper;

import com.api.erp.v1.main.dynamic.features.product.application.dto.ProductResponseDTO;
import com.api.erp.v1.main.dynamic.features.product.domain.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class IProductMapper {

    public ProductResponseDTO toResponse(Product product) {
        if (product == null) {
            return null;
        }

        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(product.getId());
        dto.setCodigo(product.getCodigo());
        dto.setDescricao(product.getDescricao());
        dto.setDescricaoDetalhada(product.getDescricaoDetalhada());
        dto.setStatus(product.getStatus());
        dto.setTipo(product.getType());
        // Set other fields as needed based on the Product entity
        return dto;
    }

    public List<ProductResponseDTO> toResponseList(List<Product> products) {
        if (products == null) {
            return List.of();
        }
        return products.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public Page<ProductResponseDTO> toResponsePage(Page<Product> page) {
        return page.map(this::toResponse);
    }
}


