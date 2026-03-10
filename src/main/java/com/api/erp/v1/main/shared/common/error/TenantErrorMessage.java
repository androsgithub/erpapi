package com.api.erp.v1.main.shared.common.error;

/**
 * Tenant-specific error messages for multi-tenancy operations.
 * <p>
 * Error codes: TENANT_001 to TENANT_099
 *
 * @author ERP System
 * @version 1.0
 */
public enum TenantErrorMessage implements IErrorMessage {

    TENANT_NOT_FOUND(
            "Tenant not found or is inactive.",
            "TENANT_001",
            ErrorType.NOT_FOUND
    ),

    DATASOURCE_NOT_CONFIGURED(
            "Datasource not configured for the specified tenant.",
            "TENANT_002",
            ErrorType.INTERNAL_ERROR
    ),

    DATASOURCE_CONNECTION_FAILED(
            "Failed to connect to tenant database.",
            "TENANT_003",
            ErrorType.SERVICE_UNAVAILABLE
    ),

    DATASOURCE_ALREADY_EXISTS(
            "Datasource already configured for this tenant.",
            "TENANT_004",
            ErrorType.CONFLICT
    ),

    INVALID_TENANT_CONFIG(
            "Invalid tenant configuration.",
            "TENANT_005",
            ErrorType.INTERNAL_ERROR
    ),

    MIGRATION_FAILED(
            "Database migration failed for tenant.",
            "TENANT_006",
            ErrorType.INTERNAL_ERROR
    ),

    TENANT_GROUP_NOT_FOUND(
            "Tenant group not found.",
            "TENANT_007",
            ErrorType.NOT_FOUND
    ),

    TENANT_ALREADY_EXISTS(
            "Tenant with this CNPJ already exists.",
            "TENANT_008",
            ErrorType.CONFLICT
    );

    private final String message;
    private final String code;
    private final ErrorType errorType;

    TenantErrorMessage(String message, String code, ErrorType errorType) {
        this.message = message;
        this.code = code;
        this.errorType = errorType;
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
    public ErrorType getErrorType() {
        return errorType;
    }

    @Override
    public String toString() {
        return "[" + code + "] - " + message;
    }
}
