package com.api.erp.v1.main.master.tenant.application.dto.response;

import java.util.Map;

public record UpdateDatasourceResponse(TenantDatasourceResponse datasource, Map<String, Object> migration,
                                       String migrationError) {

}
