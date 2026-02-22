package com.api.erp.v1.main.tenant.domain.entity.configs;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class CustomerConfig {

    @Column(name = "customer_validation_enabled", nullable = false)
    private Boolean customerValidationEnabled = false;

    @Column(name = "customer_audit_enabled", nullable = false)
    private Boolean customerAuditEnabled = false;

    @Column(name = "customer_cache_enabled", nullable = false)
    private Boolean customerCacheEnabled = false;

    @Column(name = "customer_notification_enabled", nullable = false)
    private Boolean customerNotificationEnabled = false;

    @Column(name = "customer_tenant_customization_enabled", nullable = false)
    private Boolean customerTenantCustomizationEnabled = false;

    public boolean isCustomerValidationEnabled() {
        return Boolean.TRUE.equals(customerValidationEnabled);
    }

    public boolean isCustomerAuditEnabled() {
        return Boolean.TRUE.equals(customerAuditEnabled);
    }

    public boolean isCustomerCacheEnabled() {
        return Boolean.TRUE.equals(customerCacheEnabled);
    }

    public boolean isCustomerNotificationEnabled() {
        return Boolean.TRUE.equals(customerNotificationEnabled);
    }

    public boolean isCustomerTenantCustomizationEnabled() {
        return Boolean.TRUE.equals(customerTenantCustomizationEnabled);
    }
}

