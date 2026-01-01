package com.api.erp.v1.features.produto.application.mapper;

import com.api.erp.v1.features.produto.application.dto.ProdutoResponseDTO;
import com.api.erp.v1.features.produto.domain.entity.Produto;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface IProdutoMapper {

    ProdutoResponseDTO toResponse(Produto produto);

    List<ProdutoResponseDTO> toResponseList(List<Produto> produtos);

    default Page<ProdutoResponseDTO> toResponsePage(Page<Produto> page) {
        return page.map(this::toResponse);
    }
}


