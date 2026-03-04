package com.api.erp.v1.main.datasource.routing.domain;

/**
 * DOMAIN - Exceção customizada para quando um tenant não é encontrado
 * 
 * Indica que uma tentativa de acessar um tenant resultou em falha.
 * 
 * @author ERP System
 * @version 1.0
 */
public class TenantDataSourceNotFoundException extends RuntimeException {

    private final Long tenantId;

    public TenantDataSourceNotFoundException(Long tenantId) {
        super("DataSource not found for tenant: " + tenantId);
        this.tenantId = tenantId;
    }

    public TenantDataSourceNotFoundException(Long tenantId, String message) {
        super(message);
        this.tenantId = tenantId;
    }

    public TenantDataSourceNotFoundException(Long tenantId, String message, Throwable cause) {
        super(message, cause);
        this.tenantId = tenantId;
    }

    public Long getTenantId() {
        return tenantId;
    }
}
