package com.api.erp.v1.main.master.tenant.application.dto;

import com.api.erp.v1.main.shared.domain.valueobject.CNPJ;

public record TenantResponse(
        String id,
        String name,
        String tenantSlug,
        CNPJ cnpj
) {
}
