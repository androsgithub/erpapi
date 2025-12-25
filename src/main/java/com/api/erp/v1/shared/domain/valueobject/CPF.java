package com.api.erp.v1.shared.domain.valueobject;

public class CPF {
    private final String valor;
    
    public CPF(String valor) {
        if (!isValido(valor)) {
            throw new IllegalArgumentException("CPF inválido");
        }
        this.valor = limpar(valor);
    }
    
    private String limpar(String cpf) {
        return cpf.replaceAll("[^0-9]", "");
    }
    
    private boolean isValido(String cpf) {
        String cpfLimpo = limpar(cpf);
        if (cpfLimpo.length() != 11) return false;
        
        // Validação de dígitos verificadores
        int[] pesos1 = {10, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] pesos2 = {11, 10, 9, 8, 7, 6, 5, 4, 3, 2};
        
        int soma = 0;
        for (int i = 0; i < 9; i++) {
            soma += Character.getNumericValue(cpfLimpo.charAt(i)) * pesos1[i];
        }
        int digito1 = 11 - (soma % 11);
        if (digito1 > 9) digito1 = 0;
        
        soma = 0;
        for (int i = 0; i < 10; i++) {
            soma += Character.getNumericValue(cpfLimpo.charAt(i)) * pesos2[i];
        }
        int digito2 = 11 - (soma % 11);
        if (digito2 > 9) digito2 = 0;
        
        return digito1 == Character.getNumericValue(cpfLimpo.charAt(9)) &&
               digito2 == Character.getNumericValue(cpfLimpo.charAt(10));
    }
    
    public String getValor() {
        return valor;
    }
    
    public String getFormatado() {
        return valor.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (obj instanceof CPF) {
            return valor.equals(((CPF) obj).valor);
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