package com.api.erp.v1.main.tenant.domain.entity.configs;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class ContactConfig {

    @Column(name = "contact_validation_enabled", nullable = false)
    private Boolean contactValidationEnabled = false;

    @Column(name = "contact_audit_enabled", nullable = false)
    private Boolean contactAuditEnabled = false;

    @Column(name = "contact_cache_enabled", nullable = false)
    private Boolean contactCacheEnabled = false;

    @Column(name = "contact_notification_enabled", nullable = false)
    private Boolean contactNotificationEnabled = false;

    public boolean isContactValidationEnabled() {
        return Boolean.TRUE.equals(contactValidationEnabled);
    }

    public boolean isContactAuditEnabled() {
        return Boolean.TRUE.equals(contactAuditEnabled);
    }

    public boolean isContactCacheEnabled() {
        return Boolean.TRUE.equals(contactCacheEnabled);
    }

    public boolean isContactNotificationEnabled() {
        return Boolean.TRUE.equals(contactNotificationEnabled);
    }
}
