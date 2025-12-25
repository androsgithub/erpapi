package com.api.erp.v1.shared.domain.valueobject;

import java.util.Objects;

/**
 * Value Object que representa o NCM (Nomenclatura Comum do Mercosul).
 * Formato: 8 dígitos numéricos
 */
public final class NCM {
    private final String valor;

    private NCM(String valor) {
        this.valor = validar(valor);
    }

    public static NCM de(String valor) {
        return new NCM(valor);
    }

    private String validar(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("NCM não pode ser vazio");
        }

        String ncmLimpo = valor.replaceAll("[^0-9]", "");

        if (ncmLimpo.length() != 8) {
            throw new IllegalArgumentException("NCM deve ter 8 dígitos. Valor fornecido: " + valor);
        }

        return ncmLimpo;
    }

    public String getValor() {
        return valor;
    }

    public String getValorFormatado() {
        return valor.substring(0, 4) + "." + valor.substring(4, 6) + "." + valor.substring(6);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NCM ncm = (NCM) o;
        return Objects.equals(valor, ncm.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

    @Override
    public String toString() {
        return getValorFormatado();
    }
}
