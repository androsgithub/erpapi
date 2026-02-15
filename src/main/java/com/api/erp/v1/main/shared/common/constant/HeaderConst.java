package com.api.erp.v1.main.shared.common.constant;

public class HeaderConst {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String TENANT_ID_HEADER = "X-Tenant-Id";
    
    private HeaderConst() {
        throw new AssertionError("Não instanciável");
    }
}
