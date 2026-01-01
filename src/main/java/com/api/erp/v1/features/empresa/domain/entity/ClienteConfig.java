package com.api.erp.v1.features.empresa.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class ClienteConfig {

    @Column(nullable = false)
    private Boolean clienteValidationEnabled = false;

    @Column(nullable = false)
    private Boolean clienteAuditEnabled = false;

    @Column(nullable = false)
    private Boolean clienteCacheEnabled = false;

    @Column(nullable = false)
    private Boolean clienteNotificationEnabled = false;

    @Column(nullable = false)
    private Boolean clienteTenantCustomizationEnabled = false;

    public boolean isClienteValidationEnabled() {
        return Boolean.TRUE.equals(clienteValidationEnabled);
    }

    public boolean isClienteAuditEnabled() {
        return Boolean.TRUE.equals(clienteAuditEnabled);
    }

    public boolean isClienteCacheEnabled() {
        return Boolean.TRUE.equals(clienteCacheEnabled);
    }

    public boolean isClienteNotificationEnabled() {
        return Boolean.TRUE.equals(clienteNotificationEnabled);
    }

    public boolean isClienteTenantCustomizationEnabled() {
        return Boolean.TRUE.equals(clienteTenantCustomizationEnabled);
    }
}

