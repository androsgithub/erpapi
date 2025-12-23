package com.api.erp.shared.domain.valueobject;

import java.util.Objects;

public final class CEST {
    private final String valor;

    private CEST(String valor) {
        this.valor = validar(valor);
    }

    public static CEST de(String valor) {
        return new CEST(valor);
    }

    private String validar(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("CEST não pode ser vazio");
        }

        String cestLimpo = valor.replaceAll("[^0-9]", "");

        if (cestLimpo.length() != 7) {
            throw new IllegalArgumentException("CEST deve ter 7 dígitos. Valor fornecido: " + valor);
        }

        return cestLimpo;
    }

    public String getValor() {
        return valor;
    }

    public String getValorFormatado() {
        return valor.substring(0, 2) + "." + valor.substring(2, 5) + "." + valor.substring(5);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CEST cest = (CEST) o;
        return Objects.equals(valor, cest.valor);
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