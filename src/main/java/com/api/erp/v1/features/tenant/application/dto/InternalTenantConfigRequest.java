package com.api.erp.v1.features.tenant.application.dto;

import com.api.erp.v1.shared.domain.enums.TenantCode;
import com.api.erp.v1.shared.domain.enums.TenantType;

public record InternalTenantConfigRequest(
        TenantType tenantType,
        String tenantSubdomain,
        TenantCode tenantCustomCode,
        boolean tenantFeaturesEnabled
) {
}
