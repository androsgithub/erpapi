package com.api.erp.features.empresa.domain.entity;

import com.api.erp.features.endereco.domain.entity.Endereco;
import com.api.erp.shared.domain.valueobject.CNPJ;
import com.api.erp.shared.domain.valueobject.Email;
import com.api.erp.shared.domain.valueobject.Telefone;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
public class Empresa {
    // Getters e Setters
    private String id;
    private String nome;
    private CNPJ cnpj;
    private Email email;
    private Telefone telefone;
    private Endereco endereco;
    private boolean ativa;
    private boolean requerAprovacaoGestor;
    private boolean requerEmailCorporativo;
    private List<String> dominiosPermitidos;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    public Empresa() {
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
        this.ativa = true;
    }

    public Empresa(String nome, String cnpj, String email) {
        this();
        this.nome = nome;
        this.cnpj = new CNPJ(cnpj);
        this.email = new Email(email);
    }

}
