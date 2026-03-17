package com.api.erp.v1.main.master.tenant.application.dto.response;

import com.api.erp.v1.main.master.tenant.domain.entity.DBType;

import java.time.LocalDateTime;

/**
 * TenantDatasourceResponse
 * <p>
 * DTO para retornar configuração de datasource
 */
public record TenantDatasourceResponse(
        String host,
        String databaseName,
        String username,
        String dbType,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
