package com.api.erp.v1.features.empresa.application.dto;

import com.api.erp.v1.shared.domain.enums.TenantCode;
import com.api.erp.v1.shared.domain.enums.TenantType;

public record TenantConfigRequest(
        TenantType tenantType,
        String tenantSubdomain,
        TenantCode tenantCustomCode,
        boolean tenantFeaturesEnabled
) {
}
