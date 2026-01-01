package com.api.erp.v1.features.empresa.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Embeddable
@Getter
@Setter
public class UsuarioConfig {

    @Column(nullable = false)
    private Boolean usuarioApprovalRequired = false;

    @Column(nullable = false)
    private Boolean usuarioCorporateEmailRequired = false;

    @ElementCollection
    @CollectionTable(
            name = "empresa_dominios",
            joinColumns = @JoinColumn(name = "empresa_id")
    )
    @Column(name = "dominio")
    private List<String> allowedEmailDomains;

    public boolean isUsuarioApprovalRequired() {
        return Boolean.TRUE.equals(usuarioApprovalRequired);
    }

    public boolean isUsuarioCorporateEmailRequired() {
        return Boolean.TRUE.equals(usuarioCorporateEmailRequired);
    }
}

