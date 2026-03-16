package com.api.erp.v1.main.dynamic.features.customfield.application.mapper;

import com.api.erp.v1.main.dynamic.features.customfield.application.dto.response.CustomFieldResponse;
import com.api.erp.v1.main.dynamic.features.customfield.domain.entity.CustomFieldDefinition;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ICustomFieldDefinitionMapper {

    CustomFieldResponse toResponse(CustomFieldDefinition customFieldDefinition);

    List<CustomFieldResponse> toResponseList(List<CustomFieldDefinition> customFieldDefinitions);

}
