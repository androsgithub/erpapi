package com.api.erp.v1.features.usuario.application.dto.request;

public class CreateUsuarioRequest {
    private String nomeCompleto;
    private String email;
    private String cpf;
    private String senha;
    
    // Getters e Setters
    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
}
