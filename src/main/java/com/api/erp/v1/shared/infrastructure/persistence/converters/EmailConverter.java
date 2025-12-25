package com.api.erp.v1.shared.infrastructure.persistence.converters;

import com.api.erp.v1.shared.domain.valueobject.Email;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EmailConverter implements AttributeConverter<Email, String> {

    @Override
    public String convertToDatabaseColumn(Email email) {
        return email == null ? null : email.getValor();
    }

    @Override
    public Email convertToEntityAttribute(String dbData) {
        return dbData == null ? null : new Email(dbData);
    }
}
