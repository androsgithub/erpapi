package com.api.erp.v1.shared.common.constant;

/**
 * Constantes de Headers HTTP utilizados na API
 */
public class HeaderConst {
    
    /**
     * Header para autenticação JWT
     */
    public static final String AUTHORIZATION_HEADER = "Authorization";
    
    /**
     * Prefixo para token Bearer
     */
    public static final String BEARER_PREFIX = "Bearer ";
    
    /**
     * Header para ID do tenant
     * Usado em requisições para discriminar qual tenant/banco de dados acessar
     */
    public static final String TENANT_ID_HEADER = "X-Tenant-Id";
    
    private HeaderConst() {
        throw new AssertionError("Não instanciável");
    }
}
