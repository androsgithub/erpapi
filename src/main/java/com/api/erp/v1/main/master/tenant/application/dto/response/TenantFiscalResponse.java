package com.api.erp.v1.main.master.tenant.application.dto.response;

public record TenantFiscalResponse(
        String cnpj,
        String legalName,
        String tradeName,
        String stateRegistration,
        String cityRegistration,
        String taxRegime
) {}
