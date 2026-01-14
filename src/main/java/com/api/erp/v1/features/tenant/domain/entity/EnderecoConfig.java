package com.api.erp.v1.features.tenant.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class EnderecoConfig {

    @Column(name = "endereco_validation_enabled", nullable = false)
    private Boolean enderecoValidationEnabled = false;

    @Column(name = "endereco_audit_enabled", nullable = false)
    private Boolean enderecoAuditEnabled = false;

    @Column(name = "endereco_cache_enabled", nullable = false)
    private Boolean enderecoCacheEnabled = false;

    public boolean isEnderecoValidationEnabled() {
        return Boolean.TRUE.equals(enderecoValidationEnabled);
    }

    public boolean isEnderecoAuditEnabled() {
        return Boolean.TRUE.equals(enderecoAuditEnabled);
    }

    public boolean isEnderecoCacheEnabled() {
        return Boolean.TRUE.equals(enderecoCacheEnabled);
    }
}
