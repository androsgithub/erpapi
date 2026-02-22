package com.api.erp.v1.main.shared.domain.valueobject;

import java.util.Objects;

public final class CodigoBeneficioFiscal {
    private final String valor;

    private CodigoBeneficioFiscal(String valor) {
        this.valor = validar(valor);
    }

    public static CodigoBeneficioFiscal de(String valor) {
        return new CodigoBeneficioFiscal(valor);
    }

    private String validar(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Tax benefit code cannot be empty");
        }

        String codigoLimpo = valor.trim().toUpperCase();

        if (codigoLimpo.length() > 10) {
            throw new IllegalArgumentException("Tax benefit code cannot exceed 10 characters");
        }

        if (!codigoLimpo.matches("^[A-Z0-9]+$")) {
            throw new IllegalArgumentException("Tax benefit code must contain only letters and numbers");
        }

        return codigoLimpo;
    }

    public String getValor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CodigoBeneficioFiscal that = (CodigoBeneficioFiscal) o;
        return Objects.equals(valor, that.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

    @Override
    public String toString() {
        return valor;
    }
}
