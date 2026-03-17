package com.api.erp.v1.main.master.tenant.application.dto.request.create;

import java.util.List;

public record ProvisionTenantRequest(

        String name, String email, String phone,

        Long planId,

        Boolean trial, Integer trialDays,

        List<String> allowedDomains, List<String> features

) {
}