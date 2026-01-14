package com.api.erp.v1.features.tenant.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Embeddable
@Getter
@Setter
public class UsuarioConfig {

    @Column(name = "usuario_approval_required", nullable = false)
    private Boolean usuarioApprovalRequired = false;

    @Column(name = "usuario_corporate_email_required", nullable = false)
    private Boolean usuarioCorporateEmailRequired = false;

    @ElementCollection
    @CollectionTable(
            name = "tenant_domains",
            joinColumns = @JoinColumn(name = "tenant_id")
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

