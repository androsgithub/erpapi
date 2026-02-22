package com.api.erp.v1.main.features.user.application.dto.request;

public class UpdateUserRequest {
    private String nomeCompleto;
    private String email;
    
    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
