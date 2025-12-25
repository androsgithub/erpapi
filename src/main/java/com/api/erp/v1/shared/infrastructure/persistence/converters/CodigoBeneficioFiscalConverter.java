package com.api.erp.v1.shared.infrastructure.persistence.converters;

import com.api.erp.v1.shared.domain.valueobject.CodigoBeneficioFiscal;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class CodigoBeneficioFiscalConverter implements AttributeConverter<CodigoBeneficioFiscal, String> {

    @Override
    public String convertToDatabaseColumn(CodigoBeneficioFiscal codigo) {
        return codigo == null ? null : codigo.getValor();
    }

    @Override
    public CodigoBeneficioFiscal convertToEntityAttribute(String dbData) {
        return dbData == null ? null : CodigoBeneficioFiscal.de(dbData);
    }
}
