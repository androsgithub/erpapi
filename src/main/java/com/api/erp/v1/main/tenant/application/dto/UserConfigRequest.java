package com.api.erp.v1.main.tenant.application.dto;

import java.util.List;

public record UserConfigRequest(
        boolean userApprovalRequired,
        boolean userCorporateEmailRequired,
        List<String> allowedEmailDomains
) {
}
