package com.api.erp.v1.main.shared.infrastructure.persistence.converters;

import com.api.erp.v1.main.shared.domain.valueobject.NCM;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class NCMConverter implements AttributeConverter<NCM, String> {

    @Override
    public String convertToDatabaseColumn(NCM ncm) {
        return ncm == null ? null : ncm.getValor();
    }

    @Override
    public NCM convertToEntityAttribute(String dbData) {
        return dbData == null ? null : NCM.de(dbData);
    }
}
