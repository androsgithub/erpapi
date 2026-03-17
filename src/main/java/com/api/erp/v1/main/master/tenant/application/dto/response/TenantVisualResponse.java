package com.api.erp.v1.main.master.tenant.application.dto.response;

public record TenantVisualResponse(
        String theme,
        String borderRadius,
        Boolean sidebarCollapsed,

        String colorPrimary,
        String colorSecondary,
        String colorAccent,
        String colorBackground,
        String colorText,
        String colorDanger,
        String colorSuccess,
        String colorWarning,

        String logoUrl,
        String logoLargeUrl,
        String faviconUrl,
        String emailLogoUrl,
        String backgroundImageUrl,

        String fontFamily,
        String fontCdnUrl
) {}
