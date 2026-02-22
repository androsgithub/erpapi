package com.api.erp.v1.main.shared.infrastructure.persistence.converters;

import com.api.erp.v1.main.shared.domain.valueobject.Address;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converter para Address (Value Object).
 * Serializa no formato: RUA|NUMERO|COMPLEMENTO|CIDADE|ESTADO|CEP
 * O complemento pode ser vazio/null.
 */
@Converter(autoApply = true)
public class AddressConverter implements AttributeConverter<Address, String> {

    private static final String SEPARATOR = "|";
    private static final String NULL_PLACEHOLDER = "NULL";

    @Override
    public String convertToDatabaseColumn(Address address) {
        if (address == null) {
            return null;
        }
        return address.getRua() + SEPARATOR +
               address.getNumero() + SEPARATOR +
               (address.getComplemento() != null ? address.getComplemento() : NULL_PLACEHOLDER) + SEPARATOR +
               address.getCidade() + SEPARATOR +
               address.getEstado() + SEPARATOR +
               address.getCep().getValor();
    }

    @Override
    public Address convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }

        String[] parts = dbData.split("\\|", -1);
        if (parts.length < 6) {
            throw new IllegalArgumentException("Invalid Address format: " + dbData);
        }

        String rua = parts[0];
        String numero = parts[1];
        String complemento = NULL_PLACEHOLDER.equals(parts[2]) ? null : parts[2];
        String cidade = parts[3];
        String estado = parts[4];
        String cep = parts[5];

        return new Address(rua, numero, complemento, cidade, estado, cep);
    }
}
