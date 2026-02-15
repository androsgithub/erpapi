package com.api.erp.v1.main.shared.domain.valueobject;

public class Email {

    private final String valor;
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    public Email(String valor) {
//        if (valor == null || !valor.matches(EMAIL_REGEX)) {
//            throw new IllegalArgumentException("Email inválido");
//        }
        this.valor = valor.toLowerCase();
    }

    public String getValor() {
        return valor;
    }

    public String getDominio() {
        return valor.substring(valor.indexOf('@') + 1);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (obj instanceof Email) {
            return valor.equals(((Email) obj).valor);
        }
        if (obj instanceof String) {
            return valor.equals(((String) obj).toLowerCase());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return valor.hashCode();
    }

    @Override
    public String toString() {
        return valor;
    }
}