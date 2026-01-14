package com.api.erp.v1.shared.infrastructure.persistence.converters;

import com.api.erp.v1.shared.domain.valueobject.CEST;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class CESTConverter implements AttributeConverter<CEST, String> {

    @Override
    public String convertToDatabaseColumn(CEST cest) {
        return cest == null ? null : cest.getValor();
    }

    @Override
    public CEST convertToEntityAttribute(String dbData) {
        return dbData == null ? null : CEST.de(dbData);
    }
}
