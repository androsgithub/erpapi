package com.api.erp.v1.shared.infrastructure.persistence.converters;

import com.api.erp.v1.shared.domain.valueobject.OrigemMercadoria;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class OrigemMercadoriaConverter implements AttributeConverter<OrigemMercadoria, Integer> {

    @Override
    public Integer convertToDatabaseColumn(OrigemMercadoria origem) {
        return origem == null ? null : origem.getCodigo();
    }

    @Override
    public OrigemMercadoria convertToEntityAttribute(Integer dbData) {
        return dbData == null ? null : OrigemMercadoria.doCodigo(dbData);
    }
}
