package com.api.erp.v1.main.master.tenant.application.dto.request.update;

public record UpdateTenantVisualRequest(

        String theme,
        String borderRadius,
        Boolean sidebarCollapsed,

        String colorPrimary,
        String colorSecondary,
        String colorAccent,
        String colorBackground,
        String colorText,

        String logoUrl,
        String logoLargeUrl,
        String faviconUrl,

        String fontFamily,
        String fontCdnUrl
) {
}
