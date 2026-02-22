package com.api.erp.v1.main.shared.infrastructure.exception;

import com.api.erp.v1.main.shared.common.error.TenantErrorMessage;

public class DataSourceNotConfiguredException extends RuntimeException {
    
    public DataSourceNotConfiguredException(Long tenantId) {
        super(TenantErrorMessage.DATASOURCE_NOT_CONFIGURED.getMessage() + " (Tenant: " + tenantId + ")");
    }

    public DataSourceNotConfiguredException(String message, Throwable cause) {
        super(message, cause);
    }
}
