package com.api.erp.v1.main.master.tenant.application.dto.response;

public record TenantSummaryResponse(
        Long id,
        String name,
        Boolean active,
        Boolean trial
) {
}
