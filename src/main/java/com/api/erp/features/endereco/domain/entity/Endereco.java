package com.api.erp.features.endereco.domain.entity;

import com.api.erp.shared.domain.valueobject.CEP;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
public class Endereco {
    // Getters e Setters
    private String id;
    private String rua;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;
    private CEP cep;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    public Endereco() {
        this.id = UUID.randomUUID().toString();
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
    }

    public Endereco(String rua, String numero, String bairro, String cidade, String estado, String cep) {
        this();
        this.rua = rua;
        this.numero = numero;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
        this.cep = new CEP(cep);
    }

    @Override
    public String toString() {
        return rua + ", " + numero + 
               (complemento != null ? ", " + complemento : "") + 
               " - " + bairro + " - " + cidade + "/" + estado + " - " + cep.getFormatado();
    }
}
