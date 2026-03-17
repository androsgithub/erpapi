package com.api.erp.v1.main.master.tenant.application.dto.request.update;

public record UpdateTenantAdminRequest(String name, String phone, Boolean active, Long planId) {
}
