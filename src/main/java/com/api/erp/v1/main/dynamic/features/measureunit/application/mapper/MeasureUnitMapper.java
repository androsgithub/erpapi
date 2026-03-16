package com.api.erp.v1.main.dynamic.features.measureunit.application.mapper;

import com.api.erp.v1.main.dynamic.features.measureunit.application.dto.response.MeasureUnitResponseDTO;
import com.api.erp.v1.main.dynamic.features.measureunit.domain.entity.MeasureUnit;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MeasureUnitMapper {

    public MeasureUnitResponseDTO toResponse(MeasureUnit measureUnit) {
        if (measureUnit == null) {
            return null;
        }
        return MeasureUnitResponseDTO.builder()
                .id(measureUnit.getId())
                .sigla(measureUnit.getSigla())
                .descricao(measureUnit.getDescricao())
                .build();
    }

    public List<MeasureUnitResponseDTO> toResponseList(List<MeasureUnit> unidadesMedida) {
        if (unidadesMedida == null) {
            return List.of();
        }
        return unidadesMedida.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
