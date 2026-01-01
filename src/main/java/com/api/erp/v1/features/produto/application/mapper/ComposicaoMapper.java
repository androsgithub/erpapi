package com.api.erp.v1.features.produto.application.mapper;

import com.api.erp.v1.features.produto.application.dto.ComposicaoResponseDTO;
import com.api.erp.v1.features.produto.application.dto.ProdutoResponseDTO;
import com.api.erp.v1.features.produto.domain.entity.Produto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ComposicaoMapper {

    ComposicaoResponseDTO toResponse(Produto produto);
}
