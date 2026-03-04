package com.api.erp.v1.main.tenant.domain.entity.configs;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class BusinessPartnerConfig {

    @Column(name = "business_partner_validation_enabled", nullable = false)
    private Boolean businessPartnerValidationEnabled = false;

    @Column(name = "business_partner_audit_enabled", nullable = false)
    private Boolean businessPartnerAuditEnabled = false;

    @Column(name = "business_partner_cache_enabled", nullable = false)
    private Boolean businessPartnerCacheEnabled = false;

    @Column(name = "business_partner_notification_enabled", nullable = false)
    private Boolean businessPartnerNotificationEnabled = false;

    @Column(name = "business_partner_tenant_customization_enabled", nullable = false)
    private Boolean businessPartnerTenantCustomizationEnabled = false;

    public boolean isBusinessPartnerValidationEnabled() {
        return Boolean.TRUE.equals(businessPartnerValidationEnabled);
    }

    public boolean isBusinessPartnerAuditEnabled() {
        return Boolean.TRUE.equals(businessPartnerAuditEnabled);
    }

    public boolean isBusinessPartnerCacheEnabled() {
        return Boolean.TRUE.equals(businessPartnerCacheEnabled);
    }

    public boolean isBusinessPartnerNotificationEnabled() {
        return Boolean.TRUE.equals(businessPartnerNotificationEnabled);
    }

    public boolean isBusinessPartnerTenantCustomizationEnabled() {
        return Boolean.TRUE.equals(businessPartnerTenantCustomizationEnabled);
    }
}

