package com.api.erp.v1.tenant.domain.entity.configs;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class PermissaoConfig {

    @Column(name = "permissao_validation_enabled", nullable = false)
    private Boolean permissaoValidationEnabled = false;

    @Column(name = "permissao_cache_enabled", nullable = false)
    private Boolean permissaoCacheEnabled = false;

    @Column(name = "permissao_audit_enabled", nullable = false)
    private Boolean permissaoAuditEnabled = false;

    public boolean isPermissaoValidationEnabled() {
        return Boolean.TRUE.equals(permissaoValidationEnabled);
    }

    public boolean isPermissaoCacheEnabled() {
        return Boolean.TRUE.equals(permissaoCacheEnabled);
    }

    public boolean isPermissaoAuditEnabled() {
        return Boolean.TRUE.equals(permissaoAuditEnabled);
    }
}
