package com.api.erp.v1.main.config.startup.seed;

import com.api.erp.v1.main.shared.common.error.ErrorType;
import com.api.erp.v1.main.shared.common.error.IErrorMessage;
import com.api.erp.v1.main.shared.domain.exception.BusinessException;
import com.api.erp.v1.main.shared.domain.exception.NotFoundException;
import org.springframework.http.HttpStatus;

/**
 * Tenant-specific error messages for multi-tenancy operations.
 * <p>
 * Error codes: TENANT_001 to TENANT_099
 *
 * @author ERP System
 * @version 1.0
 */
public enum InitialSeedErrorMessage implements IErrorMessage {

    FAIL_ON_INIT(
            "Internal seed failed on init",
            "INTERNAL_SEED_001",
            ErrorType.INTERNAL_ERROR
    );

    private final String message;
    private final String code;
    private final ErrorType errorType;

    InitialSeedErrorMessage(String message, String code, ErrorType errorType) {
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

