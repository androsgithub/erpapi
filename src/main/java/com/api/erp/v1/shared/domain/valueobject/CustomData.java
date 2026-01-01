package com.api.erp.v1.shared.domain.valueobject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public final class CustomData {
    private final Map<String, Object> data;

    @JsonCreator
    public CustomData(Map<String, Object> data) {
        this.data = data == null
                ? Collections.emptyMap()
                : Map.copyOf(data);
    }

    @JsonValue
    @Schema(description = "Mapa de campos personalizados dinâmicos")
    public Map<String, Object> getData() {
        return data;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type) {
        Object value = data.get(key);
        if (value == null) return null;
        return (T) value;
    }

    public <T> Optional<T> getOptional(String key, Class<T> type) {
        Object value = data.get(key);
        if (value == null || !type.isInstance(value)) {
            return Optional.empty();
        }
        return Optional.of(type.cast(value));
    }

    public boolean has(String key) {
        return data.containsKey(key);
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }
}