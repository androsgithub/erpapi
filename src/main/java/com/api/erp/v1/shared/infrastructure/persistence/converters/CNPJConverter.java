package com.api.erp.v1.shared.infrastructure.persistence.converters;

import com.api.erp.v1.shared.domain.valueobject.CNPJ;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class CNPJConverter implements AttributeConverter<CNPJ, String> {

    @Override
    public String convertToDatabaseColumn(CNPJ cnpj) {
        return cnpj == null ? null : cnpj.getValor();
    }

    @Override
    public CNPJ convertToEntityAttribute(String dbData) {
        return dbData == null ? null : new CNPJ(dbData);
    }
}
