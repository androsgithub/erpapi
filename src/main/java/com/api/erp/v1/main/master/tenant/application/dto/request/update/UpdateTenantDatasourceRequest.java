package com.api.erp.v1.main.master.tenant.application.dto.request.update;

public record UpdateTenantDatasourceRequest(
        String host,
        Integer port,
        String database,
        String username,
        String password
) {}
