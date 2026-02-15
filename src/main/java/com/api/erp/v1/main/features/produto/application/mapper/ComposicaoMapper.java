package com.api.erp.v1.main.features.produto.application.mapper;

import com.api.erp.v1.main.features.produto.application.dto.ComposicaoResponseDTO;
import com.api.erp.v1.main.features.produto.domain.entity.Produto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ComposicaoMapper {

    ComposicaoResponseDTO toResponse(Produto produto);
}
