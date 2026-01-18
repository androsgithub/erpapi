package com.api.erp.v1.tenant.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class ContatoConfig {

    @Column(name = "contato_validation_enabled", nullable = false)
    private Boolean contatoValidationEnabled = false;

    @Column(name = "contato_audit_enabled", nullable = false)
    private Boolean contatoAuditEnabled = false;

    @Column(name = "contato_cache_enabled", nullable = false)
    private Boolean contatoCacheEnabled = false;

    @Column(name = "contato_notification_enabled", nullable = false)
    private Boolean contatoNotificationEnabled = false;

    public boolean isContatoValidationEnabled() {
        return Boolean.TRUE.equals(contatoValidationEnabled);
    }

    public boolean isContatoAuditEnabled() {
        return Boolean.TRUE.equals(contatoAuditEnabled);
    }

    public boolean isContatoCacheEnabled() {
        return Boolean.TRUE.equals(contatoCacheEnabled);
    }

    public boolean isContatoNotificationEnabled() {
        return Boolean.TRUE.equals(contatoNotificationEnabled);
    }
}
