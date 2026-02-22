package com.api.erp.v1.main.features.measureunit.application.mapper;

import com.api.erp.v1.main.features.measureunit.application.dto.response.MeasureUnitResponseDTO;
import com.api.erp.v1.main.features.measureunit.domain.entity.MeasureUnit;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MeasureUnitMapper {

    MeasureUnitResponseDTO toResponse(MeasureUnit measureUnit);

    List<MeasureUnitResponseDTO> toResponseList(List<MeasureUnit> unidadesMedida);
}
