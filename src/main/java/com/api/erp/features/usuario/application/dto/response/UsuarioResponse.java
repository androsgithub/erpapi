package com.api.erp.features.usuario.application.dto.response;

import com.api.erp.features.usuario.domain.entity.StatusUsuario;
import java.time.LocalDateTime;
import java.util.UUID;

public class UsuarioResponse {
    private UUID id;
    private String nomeCompleto;
    private String email;
    private String cpf;
    private StatusUsuario status;
    private LocalDateTime dataCriacao;
    
    // Getters e Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    
    public StatusUsuario getStatus() { return status; }
    public void setStatus(StatusUsuario status) { this.status = status; }
    
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
}
