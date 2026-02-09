package com.api.erp.v1.tenant.application.dto;

import com.api.erp.v1.tenant.domain.entity.DBType;

import java.time.LocalDateTime;

/**
 * TenantDatasourceResponse
 * <p>
 * DTO para retornar configuração de datasource
 */
public record TenantDatasourceResponse(
        Long id,
        Long empresaId,
        String host,
        Integer port,
        String databaseName,
        String username,
        DBType dbType,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
