package com.api.erp.v1.tenant.application.dto;

import com.api.erp.v1.tenant.domain.entity.DBType;
import org.flywaydb.core.internal.database.DatabaseType;

/**
 * TenantDatasourceRequest
 * 
 * DTO para registrar/atualizar configuração de datasource de um tenant
 */
public record TenantDatasourceRequest(
        String host,
        Integer port,
        String databaseName,
        String username,
        String password,
        DBType dbType
) {
}
