package com.api.erp.v1.main.master.tenant.application.dto.request.create;

public record CreateTenantFeatureRequest(
        String featureKey,
        String beanName,
        String description,
        Boolean active
) {}
