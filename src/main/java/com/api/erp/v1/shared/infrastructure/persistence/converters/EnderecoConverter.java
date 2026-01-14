package com.api.erp.v1.shared.infrastructure.persistence.converters;

import com.api.erp.v1.shared.domain.valueobject.Endereco;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converter para Endereco (Value Object).
 * Serializa no formato: RUA|NUMERO|COMPLEMENTO|CIDADE|ESTADO|CEP
 * O complemento pode ser vazio/null.
 */
@Converter(autoApply = true)
public class EnderecoConverter implements AttributeConverter<Endereco, String> {

    private static final String SEPARATOR = "|";
    private static final String NULL_PLACEHOLDER = "NULL";

    @Override
    public String convertToDatabaseColumn(Endereco endereco) {
        if (endereco == null) {
            return null;
        }
        return endereco.getRua() + SEPARATOR +
               endereco.getNumero() + SEPARATOR +
               (endereco.getComplemento() != null ? endereco.getComplemento() : NULL_PLACEHOLDER) + SEPARATOR +
               endereco.getCidade() + SEPARATOR +
               endereco.getEstado() + SEPARATOR +
               endereco.getCep().getValor();
    }

    @Override
    public Endereco convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }

        String[] parts = dbData.split("\\|", -1);
        if (parts.length < 6) {
            throw new IllegalArgumentException("Formato inválido de Endereco: " + dbData);
        }

        String rua = parts[0];
        String numero = parts[1];
        String complemento = NULL_PLACEHOLDER.equals(parts[2]) ? null : parts[2];
        String cidade = parts[3];
        String estado = parts[4];
        String cep = parts[5];

        return new Endereco(rua, numero, complemento, cidade, estado, cep);
    }
}
