package com.api.erp.v1.main.shared.infrastructure.persistence.converters;

import com.api.erp.v1.main.shared.domain.valueobject.CPF;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class CPFConverter implements AttributeConverter<CPF, String> {

    @Override
    public String convertToDatabaseColumn(CPF cpf) {
        return cpf == null ? null : cpf.getValor();
    }

    @Override
    public CPF convertToEntityAttribute(String dbData) {
        return dbData == null ? null : new CPF(dbData);
    }
}
