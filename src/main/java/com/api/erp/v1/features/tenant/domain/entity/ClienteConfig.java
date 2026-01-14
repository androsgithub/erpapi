package com.api.erp.v1.features.tenant.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class ClienteConfig {

    @Column(name = "cliente_validation_enabled", nullable = false)
    private Boolean clienteValidationEnabled = false;

    @Column(name = "cliente_audit_enabled", nullable = false)
    private Boolean clienteAuditEnabled = false;

    @Column(name = "cliente_cache_enabled", nullable = false)
    private Boolean clienteCacheEnabled = false;

    @Column(name = "cliente_notification_enabled", nullable = false)
    private Boolean clienteNotificationEnabled = false;

    @Column(name = "cliente_tenant_customization_enabled", nullable = false)
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

