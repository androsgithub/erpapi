package com.api.erp.v1.main.master.tenant.application.dto.request.update;

public record UpdateTenantFiscalRequest(
        String legalName,
        String tradeName,
        String stateRegistration,
        String cityRegistration,
        String taxRegime
) {}
