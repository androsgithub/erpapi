package com.api.erp.v1.main.master.tenant.application.dto.request.update;

public record UpdateTenantFeatureRequest(
        String beanName,
        String description,
        Boolean active
) {}
