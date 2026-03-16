package com.api.erp.v1.main.shared.common.error;


public enum AuthenticationErrorMessage implements IErrorMessage {

    INVALID_CREDENTIALS(
            "Invalid email or password.",
            "AUTH_001",
            ErrorType.INVALID_ARGUMENT
    );

    private final String message;
    private final String code;
    private final ErrorType errorType;

    AuthenticationErrorMessage(String message, String code, ErrorType status) {
        this.message = message;
        this.code = code;
        this.errorType = status;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getCode() {
        return code;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    @Override
    public String toString() {
        return "[" + code + "] - " + message;
    }
}
