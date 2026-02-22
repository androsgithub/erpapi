package com.api.erp.v1.main.shared.infrastructure.persistence.converters;

import com.api.erp.v1.main.shared.domain.valueobject.UnidadeTributavel;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converter para UnidadeTributavel.
 * Armazena na forma "CODIGO|DESCRICAO" para permitir reconstrução completa do value object.
 */
@Converter(autoApply = true)
public class UnidadeTributavelConverter implements AttributeConverter<UnidadeTributavel, String> {

    private static final String SEPARATOR = "|";

    @Override
    public String convertToDatabaseColumn(UnidadeTributavel unidade) {
        if (unidade == null) {
            return null;
        }
        return unidade.getCodigo() + SEPARATOR + unidade.getDescricao();
    }

    @Override
    public UnidadeTributavel convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        
        String[] parts = dbData.split("\\|", 2);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid UnidadeTributavel format: " + dbData);
        }
        
        return UnidadeTributavel.de(parts[0], parts[1]);
    }
}
