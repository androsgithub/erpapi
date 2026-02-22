package com.api.erp.v1.main.tenant.domain.entity.configs;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class ProductConfig {

    @Column(name = "product_validation_enabled", nullable = false)
    private Boolean productValidationEnabled = false;

    @Column(name = "product_audit_enabled", nullable = false)
    private Boolean productAuditEnabled = false;

    @Column(name = "product_cache_enabled", nullable = false)
    private Boolean productCacheEnabled = false;

    public boolean isProductValidationEnabled() {
        return Boolean.TRUE.equals(productValidationEnabled);
    }

    public boolean isProductAuditEnabled() {
        return Boolean.TRUE.equals(productAuditEnabled);
    }

    public boolean isProductCacheEnabled() {
        return Boolean.TRUE.equals(productCacheEnabled);
    }
}
