package com.api.erp.v1.main.master.tenant.application.dto.response;

public record TenantSecurityResponse(
        Integer minPasswordLength,
        Boolean requireUppercase,
        Boolean requireNumber,
        Boolean requireSpecial,
        Integer passwordExpirationDays,
        Integer maxLoginAttempts,
        Integer lockoutDurationMin
) {}