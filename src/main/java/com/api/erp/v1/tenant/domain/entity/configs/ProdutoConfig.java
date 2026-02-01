package com.api.erp.v1.tenant.domain.entity.configs;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class ProdutoConfig {

    @Column(name = "produto_validation_enabled", nullable = false)
    private Boolean produtoValidationEnabled = false;

    @Column(name = "produto_audit_enabled", nullable = false)
    private Boolean produtoAuditEnabled = false;

    @Column(name = "produto_cache_enabled", nullable = false)
    private Boolean produtoCacheEnabled = false;

    public boolean isProdutoValidationEnabled() {
        return Boolean.TRUE.equals(produtoValidationEnabled);
    }

    public boolean isProdutoAuditEnabled() {
        return Boolean.TRUE.equals(produtoAuditEnabled);
    }

    public boolean isProdutoCacheEnabled() {
        return Boolean.TRUE.equals(produtoCacheEnabled);
    }
}
