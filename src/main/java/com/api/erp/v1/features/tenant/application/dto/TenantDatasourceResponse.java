package com.api.erp.v1.features.tenant.application.dto;

import java.time.LocalDateTime;

/**
 * TenantDatasourceResponse
 * 
 * DTO para retornar configuração de datasource
 */
public record TenantDatasourceResponse(
        Long id,
        Long empresaId,
        String host,
        Integer port,
        String databaseName,
        String username,
        String driverClassName,
        String dialect,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
