package com.api.erp.v1.features.empresa.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class ContatoConfig {

    @Column(nullable = false)
    private Boolean contatoValidationEnabled = false;

    @Column(nullable = false)
    private Boolean contatoAuditEnabled = false;

    @Column(nullable = false)
    private Boolean contatoCacheEnabled = false;

    @Column(nullable = false)
    private Boolean contatoFormatValidationEnabled = false;

    @Column(nullable = false)
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

    public boolean isContatoFormatValidationEnabled() {
        return Boolean.TRUE.equals(contatoFormatValidationEnabled);
    }

    public boolean isContatoNotificationEnabled() {
        return Boolean.TRUE.equals(contatoNotificationEnabled);
    }
}
