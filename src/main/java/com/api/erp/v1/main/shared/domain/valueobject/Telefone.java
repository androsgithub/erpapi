package com.api.erp.v1.main.shared.domain.valueobject;

public class Telefone {
    private final String valor;
    
    public Telefone(String valor) {
        if (!isValido(valor)) {
            throw new IllegalArgumentException("Invalid phone number");
        }
        this.valor = limpar(valor);
    }
    
    private String limpar(String telefone) {
        return telefone.replaceAll("[^0-9]", "");
    }
    
    private boolean isValido(String telefone) {
        String telefoneLimpo = limpar(telefone);
        // Aceita telefones com 10 dígitos (celular/fixo) ou 11 dígitos (celular com 9º dígito)
        return (telefoneLimpo.length() == 10 || telefoneLimpo.length() == 11) && 
               telefoneLimpo.matches("\\d{10,11}");
    }
    
    public String getValor() {
        return valor;
    }
    
    public String getFormatado() {
        if (valor.length() == 10) {
            // Formato (XX) XXXX-XXXX
            return valor.replaceAll("(\\d{2})(\\d{4})(\\d{4})", "($1) $2-$3");
        } else {
            // Formato (XX) XXXXX-XXXX
            return valor.replaceAll("(\\d{2})(\\d{5})(\\d{4})", "($1) $2-$3");
        }
    }
    
    public boolean isCelular() {
        return valor.length() == 11;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (obj instanceof Telefone) {
            return valor.equals(((Telefone) obj).valor);
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
