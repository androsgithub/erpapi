package com.api.erp.v1.main.shared.common.error;

import com.api.erp.v1.main.shared.domain.exception.BusinessException;
import com.api.erp.v1.main.shared.domain.exception.NotFoundException;
import org.springframework.http.HttpStatus;

/**
 * Tenant-specific error messages for multi-tenancy operations.
 * 
 * Error codes: TENANT_001 to TENANT_099
 * 
 * @author ERP System
 * @version 1.0
 */
public enum TenantErrorMessage implements IErrorMessage {

    TENANT_NOT_FOUND(
        "Tenant not found or is inactive.",
        "TENANT_001",
        HttpStatus.NOT_FOUND
    ),

    DATASOURCE_NOT_CONFIGURED(
        "Datasource not configured for the specified tenant.",
        "TENANT_002",
        HttpStatus.BAD_REQUEST
    ),

    DATASOURCE_CONNECTION_FAILED(
        "Failed to connect to tenant database.",
        "TENANT_003",
        HttpStatus.SERVICE_UNAVAILABLE
    ),

    DATASOURCE_ALREADY_EXISTS(
        "Datasource already configured for this tenant.",
        "TENANT_004",
        HttpStatus.CONFLICT
    ),

    INVALID_TENANT_CONFIG(
        "Invalid tenant configuration.",
        "TENANT_005",
        HttpStatus.BAD_REQUEST
    ),

    MIGRATION_FAILED(
        "Database migration failed for tenant.",
        "TENANT_006",
        HttpStatus.INTERNAL_SERVER_ERROR
    ),

    TENANT_GROUP_NOT_FOUND(
        "Tenant group not found.",
        "TENANT_007",
        HttpStatus.NOT_FOUND
    ),

    TENANT_ALREADY_EXISTS(
        "Tenant with this CNPJ already exists.",
        "TENANT_008",
        HttpStatus.CONFLICT
    );

    private final String message;
    private final String code;
    private final HttpStatus status;

    TenantErrorMessage(String message, String code, HttpStatus status) {
        this.message = message;
        this.code = code;
        this.status = status;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public BusinessException toBusinessException() {
        return new BusinessException(this.status, this.message);
    }

    @Override
    public NotFoundException toNotFoundException() {
        if (!this.status.equals(HttpStatus.NOT_FOUND)) {
            throw new IllegalStateException("This error is not of type NOT_FOUND: " + this.code);
        }
        return new NotFoundException(this.message);
    }

    @Override
    public String toString() {
        return "[" + code + "] - " + message;
    }
}
