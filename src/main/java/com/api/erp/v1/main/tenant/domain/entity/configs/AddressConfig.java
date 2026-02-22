package com.api.erp.v1.main.tenant.domain.entity.configs;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class AddressConfig {

    @Column(name = "address_validation_enabled", nullable = false)
    private Boolean addressValidationEnabled = false;

    @Column(name = "address_audit_enabled", nullable = false)
    private Boolean addressAuditEnabled = false;

    @Column(name = "address_cache_enabled", nullable = false)
    private Boolean addressCacheEnabled = false;

    public boolean isAddressValidationEnabled() {
        return Boolean.TRUE.equals(addressValidationEnabled);
    }

    public boolean isAddressAuditEnabled() {
        return Boolean.TRUE.equals(addressAuditEnabled);
    }

    public boolean isAddressCacheEnabled() {
        return Boolean.TRUE.equals(addressCacheEnabled);
    }
}
