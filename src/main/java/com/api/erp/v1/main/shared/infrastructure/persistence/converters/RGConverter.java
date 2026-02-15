package com.api.erp.v1.main.shared.infrastructure.persistence.converters;

import com.api.erp.v1.main.shared.domain.valueobject.RG;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RGConverter implements AttributeConverter<RG, String> {

    @Override
    public String convertToDatabaseColumn(RG rg) {
        return rg == null ? null : rg.getValor();
    }

    @Override
    public RG convertToEntityAttribute(String dbData) {
        return dbData == null ? null : new RG(dbData);
    }
}
