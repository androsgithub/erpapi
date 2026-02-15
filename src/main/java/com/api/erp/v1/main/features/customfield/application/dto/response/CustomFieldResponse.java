package com.api.erp.v1.main.features.customfield.application.dto.response;

import com.api.erp.v1.main.shared.domain.enums.CustomFieldType;

import java.util.Map;

public record CustomFieldResponse(
        Long id,
        String target,
        String fieldKey,
        String label,
        CustomFieldType fieldType,
        Boolean required,
        Integer fieldOrder,
        Map<String, Object> metadata,
        Boolean active
) {
}
