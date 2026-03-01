package com.api.erp.v1.main.tenant.application.dto;

import com.api.erp.v1.main.tenant.domain.entity.DBType;

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
        String dbType
) {
}
