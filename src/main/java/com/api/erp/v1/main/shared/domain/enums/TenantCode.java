package com.api.erp.v1.main.shared.domain.enums;

public enum TenantCode {
    DEFAULT("default", "Padrão"),
    HECE("hece", "Codigo de operação identificador da empresa"),
    NOOP("noop", "Codigo de operação identificador para não-operações");

    private final String code;
    private final String description;

    TenantCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static TenantCode fromCode(String code) {
        for (TenantCode type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return DEFAULT;
    }
}
