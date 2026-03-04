package com.api.erp.v1.main.shared.domain.valueobject;

public class RG {

    private final String valor;

    public RG(String valor) {
        if (!isValido(valor)) {
            throw new IllegalArgumentException("Invalid RG");
        }
        this.valor = limpar(valor);
    }

    private String limpar(String rg) {
        return rg.replaceAll("[^0-9Xx]", "").toUpperCase();
    }

    /**
     * Validation estrutural de RG
     * - Aceita dígito X
     * - Tamanho entre 7 e 14 (varia por estado)
     */
    private boolean isValido(String rg) {
        if (rg == null) return false;

        String rgLimpo = limpar(rg);

        if (rgLimpo.length() < 7 || rgLimpo.length() > 14) {
            return false;
        }

        // Evita RG com todos os caracteres iguais
        char primeiro = rgLimpo.charAt(0);
        boolean todosIguais = true;

        for (char c : rgLimpo.toCharArray()) {
            if (c != primeiro) {
                todosIguais = false;
                break;
            }
        }

        return !todosIguais;
    }

    public String getValor() {
        return valor;
    }

    /**
     * RG não tem formatação padrão nacional
     * Returns o valor limpo
     */
    public String getFormatado() {
        return valor;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;

        if (obj instanceof RG) {
            return valor.equals(((RG) obj).valor);
        }

        if (obj instanceof String) {
            return valor.equals(limpar((String) obj));
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
