package com.api.erp.v1.shared.domain.enums;

public enum TenantType {
    DEFAULT("default", "Configuração Padrão"),
    HECE("hece", "Configuração para empresa Hece - máquinas");

    private final String code;
    private final String description;

    TenantType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static TenantType fromCode(String code) {
        for (TenantType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return DEFAULT;
    }
}