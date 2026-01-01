package com.api.erp.v1.features.empresa.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class PermissaoConfig {

    @Column(nullable = false)
    private Boolean permissaoValidationEnabled = false;

    @Column(nullable = false)
    private Boolean permissaoCacheEnabled = false;

    @Column(nullable = false)
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
