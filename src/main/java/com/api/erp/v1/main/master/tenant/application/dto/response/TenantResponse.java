package com.api.erp.v1.main.master.tenant.application.dto.response;

import java.time.LocalDateTime;

public record TenantResponse(Long id, String name, String email, String phone, Boolean active, Boolean trial,
                             LocalDateTime trialExpiresAt, LocalDateTime createdAt) {
}