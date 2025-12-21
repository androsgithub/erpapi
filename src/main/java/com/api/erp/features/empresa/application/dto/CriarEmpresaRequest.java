package com.api.erp.features.empresa.application.dto;

import java.time.LocalDateTime;
import java.util.List;

public class CriarEmpresaRequest {
    private String nome;
    private String cnpj;
    private String email;
    private String telefone;
    private String enderecoId;
    private boolean requerAprovacaoGestor;
    private boolean requerEmailCorporativo;
    private List<String> dominiosPermitidos;

    // Construtores
    public CriarEmpresaRequest() {}

    public CriarEmpresaRequest(String nome, String cnpj, String email) {
        this.nome = nome;
        this.cnpj = cnpj;
        this.email = email;
    }

    // Getters e Setters
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEnderecoId() {
        return enderecoId;
    }

    public void setEnderecoId(String enderecoId) {
        this.enderecoId = enderecoId;
    }

    public boolean isRequerAprovacaoGestor() {
        return requerAprovacaoGestor;
    }

    public void setRequerAprovacaoGestor(boolean requerAprovacaoGestor) {
        this.requerAprovacaoGestor = requerAprovacaoGestor;
    }

    public boolean isRequerEmailCorporativo() {
        return requerEmailCorporativo;
    }

    public void setRequerEmailCorporativo(boolean requerEmailCorporativo) {
        this.requerEmailCorporativo = requerEmailCorporativo;
    }

    public List<String> getDominiosPermitidos() {
        return dominiosPermitidos;
    }

    public void setDominiosPermitidos(List<String> dominiosPermitidos) {
        this.dominiosPermitidos = dominiosPermitidos;
    }
}
