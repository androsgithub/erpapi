package com.api.erp.v1.shared.infrastructure.exception;

public class DataSourceNotConfiguredException extends RuntimeException {
    
    public DataSourceNotConfiguredException(Long tenantId) {
        super(String.format(
            "O banco de dados do cliente não está configurado. " +
            "Entre em contato com o administrador. (Tenant: %s)", 
            tenantId
        ));
    }

    public DataSourceNotConfiguredException(String message, Throwable cause) {
        super(message, cause);
    }
}
