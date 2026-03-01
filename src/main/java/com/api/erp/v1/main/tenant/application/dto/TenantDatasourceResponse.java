package com.api.erp.v1.main.tenant.application.dto;

import com.api.erp.v1.main.tenant.domain.entity.DBType;

import java.time.LocalDateTime;

/**
 * TenantDatasourceResponse
 * <p>
 * DTO para retornar configuração de datasource
 */
public record TenantDatasourceResponse(
        Long id,
        Long tenantId,
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
