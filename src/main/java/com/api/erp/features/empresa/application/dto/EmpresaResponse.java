package com.api.erp.features.empresa.application.dto;

import java.time.LocalDateTime;
import java.util.List;

public class EmpresaResponse {
    private String id;
    private String nome;
    private String cnpj;
    private String email;
    private String telefone;
    private String enderecoId;
    private boolean ativa;
    private boolean requerAprovacaoGestor;
    private boolean requerEmailCorporativo;
    private List<String> dominiosPermitidos;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    // Construtores
    public EmpresaResponse() {}

    public EmpresaResponse(String id, String nome, String cnpj, String email) {
        this.id = id;
        this.nome = nome;
        this.cnpj = cnpj;
        this.email = email;
    }

    // Getters e Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public boolean isAtiva() {
        return ativa;
    }

    public void setAtiva(boolean ativa) {
        this.ativa = ativa;
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

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }
}
