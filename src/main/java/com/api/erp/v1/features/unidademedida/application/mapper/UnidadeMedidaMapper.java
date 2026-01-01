package com.api.erp.v1.features.unidademedida.application.mapper;

import com.api.erp.v1.features.unidademedida.application.dto.response.UnidadeMedidaResponseDTO;
import com.api.erp.v1.features.unidademedida.domain.entity.UnidadeMedida;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UnidadeMedidaMapper {

    UnidadeMedidaResponseDTO toResponse(UnidadeMedida unidadeMedida);

    List<UnidadeMedidaResponseDTO> toResponseList(List<UnidadeMedida> unidadesMedida);
}
