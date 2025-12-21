package com.api.erp.features.usuario.application.dto.request;

public class UpdateUsuarioRequest {
    private String nomeCompleto;
    private String email;
    
    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
