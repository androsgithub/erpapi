package com.api.erp.v1.main.master.tenant.application.dto.request.update;

public record UpdateTenantConfigRequest(
        String appName,
        String appDescription,
        String supportEmail,
        Boolean multiBranch,
        Boolean allowApiAccess,
        Boolean whiteLabel,
        Boolean maintenanceMode
) {}
