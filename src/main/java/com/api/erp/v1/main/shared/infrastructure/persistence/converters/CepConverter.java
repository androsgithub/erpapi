package com.api.erp.v1.main.shared.infrastructure.persistence.converters;

import com.api.erp.v1.main.shared.domain.valueobject.CEP;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class CepConverter implements AttributeConverter<CEP, String> {

    @Override
    public String convertToDatabaseColumn(CEP cep) {
        return cep == null ? null : cep.getValor();
    }

    @Override
    public CEP convertToEntityAttribute(String dbData) {
        return dbData == null ? null : new CEP(dbData);
    }
}
