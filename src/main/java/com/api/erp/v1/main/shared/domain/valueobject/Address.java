package com.api.erp.v1.main.shared.domain.valueobject;

public class Address {
    private final String rua;
    private final String numero;
    private final String complemento;
    private final String cidade;
    private final String estado;
    private final CEP cep;

    public Address(String rua, String numero, String cidade, String estado, String cep) {
        this(rua, numero, null, cidade, estado, cep);
    }

    public Address(String rua, String numero, String complemento, String cidade, String estado, String cep) {
        if (rua == null || rua.isBlank()) {
            throw new IllegalArgumentException("Street is required");
        }
        if (numero == null || numero.isBlank()) {
            throw new IllegalArgumentException("Number is required");
        }
        if (cidade == null || cidade.isBlank()) {
            throw new IllegalArgumentException("City is required");
        }
        if (estado == null || estado.isBlank()) {
            throw new IllegalArgumentException("State is required");
        }
        if (estado.length() != 2) {
            throw new IllegalArgumentException("Estado deve ter 2 caracteres");
        }

        this.rua = rua;
        this.numero = numero;
        this.complemento = complemento;
        this.cidade = cidade;
        this.estado = estado.toUpperCase();
        this.cep = new CEP(cep);
    }

    public String getRua() {
        return rua;
    }

    public String getNumero() {
        return numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public String getCidade() {
        return cidade;
    }

    public String getEstado() {
        return estado;
    }

    public CEP getCep() {
        return cep;
    }

    public String getFormatado() {
        StringBuilder sb = new StringBuilder();
        sb.append(rua).append(", ").append(numero);
        if (complemento != null && !complemento.isBlank()) {
            sb.append(" - ").append(complemento);
        }
        sb.append(" - ").append(cidade).append(", ").append(estado).append(" - ").append(cep.getFormatado());
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (obj instanceof Address) {
            Address other = (Address) obj;
            return rua.equals(other.rua) &&
                    numero.equals(other.numero) &&
                    cidade.equals(other.cidade) &&
                    estado.equals(other.estado) &&
                    cep.equals(other.cep);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (rua + numero + cidade + estado + cep.getValor()).hashCode();
    }

    @Override
    public String toString() {
        return getFormatado();
    }
}
