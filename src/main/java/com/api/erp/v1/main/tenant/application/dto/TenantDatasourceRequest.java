package com.api.erp.v1.main.tenant.application.dto;

import com.api.erp.v1.main.tenant.domain.entity.DBType;

/**
 * TenantDatasourceRequest
 * 
 * DTO para registrar/atualizar configuração de datasource de um tenant
 * 
 * Propriedades:
 * - host: Host do banco de dados
 * - port: Porta do banco de dados
 * - databaseName: Nome do banco de dados
 * - username: Usuário de autenticação
 * - password: Senha de autenticação
 * - dbType: Tipo de banco (MySQL, PostgreSQL, Oracle, etc)
 * - runMigrations: OPCIONAL - Se true, enfileira migrações após atualizar
 *                  Padrão: false (apenas atualiza sem enfileirar)
 */
public record TenantDatasourceRequest(
        String host,
        Integer port,
        String databaseName,
        String username,
        String password,
        String dbType,
        Boolean runMigrations
) {
    
    /**
     * Construtor alternativo sem runMigrations (padrão = false)
     */
    public TenantDatasourceRequest(String host, Integer port, String databaseName, 
                                   String username, String password, String dbType) {
        this(host, port, databaseName, username, password, dbType, false);
    }
}
