package com.api.erp.v1.features.tenant.application.dto;

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
        String driverClassName,
        String dialect
) {
}
