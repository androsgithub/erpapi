package com.api.erp.v1.main.features.businesspartner.application.dto;

public record BusinessPartnerPreferenciasDto(
        String emailPrincipal,
        String emailNfe,
        Boolean enviarEmail,
        Boolean malaDireta
) {
}

