package com.api.erp.v1.shared.infrastructure.persistence.converters;

import com.api.erp.v1.shared.domain.valueobject.Telefone;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TelefoneConverter implements AttributeConverter<Telefone, String> {

    @Override
    public String convertToDatabaseColumn(Telefone telefone) {
        return telefone == null ? null : telefone.getValor();
    }

    @Override
    public Telefone convertToEntityAttribute(String dbData) {
        return dbData == null ? null : new Telefone(dbData);
    }
}
