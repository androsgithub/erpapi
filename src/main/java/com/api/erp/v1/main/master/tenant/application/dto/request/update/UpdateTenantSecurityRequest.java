package com.api.erp.v1.main.master.tenant.application.dto.request.update;

public record UpdateTenantSecurityRequest(
        Integer minPasswordLength,
        Boolean requireUppercase,
        Boolean requireNumber,
        Boolean requireSpecial,
        Integer passwordExpirationDays,
        Integer maxLoginAttempts,
        Integer lockoutDurationMin
) {}
