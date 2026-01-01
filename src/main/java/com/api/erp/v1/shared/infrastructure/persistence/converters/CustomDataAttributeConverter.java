package com.api.erp.v1.shared.infrastructure.persistence.converters;

import com.api.erp.v1.shared.domain.valueobject.CustomData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Converter
public class CustomDataAttributeConverter implements AttributeConverter<CustomData, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(CustomData attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(attribute.getData());
        } catch (JsonProcessingException e) {
            log.error("Erro ao serializar CustomData para JSON", e);
            throw new RuntimeException("Erro ao converter CustomData para JSON", e);
        }
    }

    @Override
    public CustomData convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return new CustomData(Collections.emptyMap());
        }

        try {
            Map<String, Object> data = objectMapper.readValue(
                    dbData,
                    new TypeReference<Map<String, Object>>() {
                    }
            );
            return new CustomData(data);
        } catch (JsonProcessingException e) {
            log.error("Erro ao deserializar JSON para CustomData", e);
            return new CustomData(Collections.emptyMap());
        }
    }
}
