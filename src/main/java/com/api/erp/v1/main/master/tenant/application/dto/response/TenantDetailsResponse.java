package com.api.erp.v1.main.master.tenant.application.dto.response;

import java.time.LocalDateTime;
import java.util.Set;

public record TenantDetailsResponse(Long id, String name, String email, String phone, Boolean active, Boolean trial,
                                    LocalDateTime trialExpiresAt, TenantPlanResponse plan, Set<String> allowedDomains,
                                    Set<String> features, LocalDateTime createdAt, LocalDateTime updatedAt) {
}