package com.api.erp.v1.main.master.tenant.application.dto.response;

import java.time.LocalDateTime;

public record TenantConfigResponse(
        String slug,
        String appName,
        String appDescription,
        String supportEmail,
        Boolean multiBranch,
        Boolean allowApiAccess,
        Boolean whiteLabel,
        Boolean maintenanceMode,
        Boolean onboardingDone,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
