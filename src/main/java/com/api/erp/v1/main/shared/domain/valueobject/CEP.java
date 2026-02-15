package com.api.erp.v1.main.shared.domain.valueobject;

public class CEP {
    private final String valor;
    
    public CEP(String valor) {
        if (!isValido(valor)) {
            throw new IllegalArgumentException("CEP inválido");
        }
        this.valor = limpar(valor);
    }
    
    private String limpar(String cep) {
        return cep.replaceAll("[^0-9]", "");
    }
    
    private boolean isValido(String cep) {
        String cepLimpo = limpar(cep);
        return cepLimpo.length() == 8 && cepLimpo.matches("\\d{8}");
    }
    
    public String getValor() {
        return valor;
    }
    
    public String getFormatado() {
        return valor.replaceAll("(\\d{5})(\\d{3})", "$1-$2");
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (obj instanceof CEP) {
            return valor.equals(((CEP) obj).valor);
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
