package com.api.erp.v1.features.customfield.application.dto.request;

import com.api.erp.v1.shared.domain.enums.CustomFieldType;

import java.util.Map;

public record CreateCustomFieldRequest(
        String target,
        String fieldKey,
        String label,
        CustomFieldType fieldType,
        Boolean required,
        Integer fieldOrder,
        Map<String, Object> metadata
) {
}

