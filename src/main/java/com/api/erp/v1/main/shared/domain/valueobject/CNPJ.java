package com.api.erp.v1.main.shared.domain.valueobject;

import com.api.erp.v1.main.shared.domain.exception.ValidationException;

public class CNPJ {
    private final String valor;
    
    public CNPJ(String valor) {
        if (!isValido(valor)) {
            throw new ValidationException("cnpj", "Invalid CNPJ. Check format and check digits.");
        }
        this.valor = limpar(valor);
    }
    
    private String limpar(String cnpj) {
        return cnpj.replaceAll("[^0-9]", "");
    }
    
    private boolean isValido(String cnpj) {
        String cnpjLimpo = limpar(cnpj);
        
        if (cnpjLimpo.length() != 14) {
            return false;
        }
        
        // Verifica se todos os dígitos são iguais
        if (cnpjLimpo.matches("(\\d)\\1{13}")) {
            return false;
        }
        
        // Validação de dígitos verificadores
        int[] pesos1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] pesos2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        
        int soma = 0;
        for (int i = 0; i < 12; i++) {
            soma += Character.getNumericValue(cnpjLimpo.charAt(i)) * pesos1[i];
        }
        int digito1 = 11 - (soma % 11);
        if (digito1 > 9) digito1 = 0;
        
        soma = 0;
        for (int i = 0; i < 13; i++) {
            soma += Character.getNumericValue(cnpjLimpo.charAt(i)) * pesos2[i];
        }
        int digito2 = 11 - (soma % 11);
        if (digito2 > 9) digito2 = 0;
        
        return digito1 == Character.getNumericValue(cnpjLimpo.charAt(12)) &&
               digito2 == Character.getNumericValue(cnpjLimpo.charAt(13));
    }
    
    public String getValor() {
        return valor;
    }
    
    public String getFormatado() {
        return valor.replaceAll("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (obj instanceof CNPJ) {
            return valor.equals(((CNPJ) obj).valor);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return valor.hashCode();
    }
    
    @Override
    public String toString() {
        return getFormatado();
    }
}
