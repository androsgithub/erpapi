package com.api.erp.v1.features.camposcustom.application.dto.response;

import com.api.erp.v1.shared.domain.enums.CustomFieldType;

import java.util.Map;

public record CustomFieldResponse(
        Long id,
        String tableName,
        String fieldKey,
        String label,
        CustomFieldType fieldType,
        Boolean required,
        Integer fieldOrder,
        Map<String, Object> metadata,
        Boolean active
) {
}
