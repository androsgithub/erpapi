package com.api.erp.v1.features.camposcustom.application.mapper;

import com.api.erp.v1.features.camposcustom.application.dto.response.CustomFieldResponse;
import com.api.erp.v1.features.camposcustom.domain.entity.CustomFieldDefinition;
import com.api.erp.v1.features.cliente.application.dto.response.ClienteSimpleResponseDto;
import com.api.erp.v1.features.cliente.domain.entity.Cliente;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ICustomFieldDefinitionMapper {

    CustomFieldResponse toResponse(CustomFieldDefinition customFieldDefinition);

    List<CustomFieldResponse> toResponseList(List<CustomFieldDefinition> customFieldDefinitions);

}
