package com.api.erp.v1.main.dynamic.features.customfield.application.mapper;

import com.api.erp.v1.main.dynamic.features.customfield.application.dto.response.CustomFieldResponse;
import com.api.erp.v1.main.dynamic.features.customfield.domain.entity.CustomFieldDefinition;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ICustomFieldDefinitionMapper {

    public CustomFieldResponse toResponse(CustomFieldDefinition customFieldDefinition) {
        if (customFieldDefinition == null) {
            return null;
        }
        return new CustomFieldResponse(
                customFieldDefinition.getId(),
                customFieldDefinition.getTarget(),
                customFieldDefinition.getFieldKey(),
                customFieldDefinition.getLabel(),
                customFieldDefinition.getFieldType(),
                customFieldDefinition.getRequired() != null ? customFieldDefinition.getRequired() : false,
                customFieldDefinition.getFieldOrder(),
                customFieldDefinition.getMetadata(),
                customFieldDefinition.getActive() != null ? customFieldDefinition.getActive() : false
        );
    }

    public List<CustomFieldResponse> toResponseList(List<CustomFieldDefinition> customFieldDefinitions) {
        if (customFieldDefinitions == null) {
            return List.of();
        }
        return customFieldDefinitions.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
