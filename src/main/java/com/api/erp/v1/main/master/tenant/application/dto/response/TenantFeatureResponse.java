package com.api.erp.v1.main.master.tenant.application.dto.response;

import java.time.LocalDateTime;

public record TenantFeatureResponse(
        Long id,
        String featureKey,
        String beanName,
        String description,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
