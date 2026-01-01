package com.api.erp.v1.features.empresa.application.mapper;

import com.api.erp.v1.features.empresa.application.dto.EmpresaResponse;
import com.api.erp.v1.features.empresa.domain.entity.Empresa;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EmpresaMapper {

    EmpresaResponse toResponse(Empresa empresa);

    List<EmpresaResponse> toResponseList(List<Empresa> empresas);
}
