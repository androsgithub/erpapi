package com.api.erp.features.usuario.domain.entity;

import com.api.erp.shared.domain.valueobject.CPF;
import com.api.erp.shared.domain.valueobject.Email;
import java.time.LocalDateTime;
import java.util.UUID;

public class Usuario {
    private UUID id;
    private String nomeCompleto;
    private Email email;
    private CPF cpf;
    private String senhaHash;
    private StatusUsuario status;
    private UUID aprovadoPor;
    private LocalDateTime dataAprovacao;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    
    // Builder Pattern
    public static class Builder {
        private Usuario usuario = new Usuario();
        
        public Builder id(UUID id) {
            usuario.id = id;
            return this;
        }
        
        public Builder nomeCompleto(String nome) {
            usuario.nomeCompleto = nome;
            return this;
        }
        
        public Builder email(Email email) {
            usuario.email = email;
            return this;
        }
        
        public Builder cpf(CPF cpf) {
            usuario.cpf = cpf;
            return this;
        }
        
        public Builder senhaHash(String senhaHash) {
            usuario.senhaHash = senhaHash;
            return this;
        }
        
        public Builder status(StatusUsuario status) {
            usuario.status = status;
            return this;
        }
        
        public Usuario build() {
            usuario.id = usuario.id != null ? usuario.id : UUID.randomUUID();
            usuario.status = usuario.status != null ? usuario.status : StatusUsuario.ATIVO;
            usuario.dataCriacao = LocalDateTime.now();
            usuario.dataAtualizacao = LocalDateTime.now();
            return usuario;
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    // Métodos de negócio
    public void aprovar(UUID gestorId) {
        if (this.status != StatusUsuario.PENDENTE_APROVACAO) {
            throw new IllegalStateException("Usuário não está pendente de aprovação");
        }
        this.status = StatusUsuario.ATIVO;
        this.aprovadoPor = gestorId;
        this.dataAprovacao = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
    }
    
    public void rejeitar() {
        if (this.status != StatusUsuario.PENDENTE_APROVACAO) {
            throw new IllegalStateException("Usuário não está pendente de aprovação");
        }
        this.status = StatusUsuario.REJEITADO;
        this.dataAtualizacao = LocalDateTime.now();
    }
    
    public void inativar() {
        this.status = StatusUsuario.INATIVO;
        this.dataAtualizacao = LocalDateTime.now();
    }
    
    public boolean isPendente() {
        return this.status == StatusUsuario.PENDENTE_APROVACAO;
    }
    
    // Getters
    public UUID getId() { return id; }
    public String getNomeCompleto() { return nomeCompleto; }
    public Email getEmail() { return email; }
    public CPF getCpf() { return cpf; }
    public String getSenhaHash() { return senhaHash; }
    public StatusUsuario getStatus() { return status; }
    public UUID getAprovadoPor() { return aprovadoPor; }
    public LocalDateTime getDataAprovacao() { return dataAprovacao; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    
    // Setters necessários
    public void setStatus(StatusUsuario status) {
        this.status = status;
        this.dataAtualizacao = LocalDateTime.now();
    }
    
    public void setAprovadoPor(UUID aprovadoPor) {
        this.aprovadoPor = aprovadoPor;
    }
    
    public void setDataAprovacao(LocalDateTime dataAprovacao) {
        this.dataAprovacao = dataAprovacao;
    }
}
