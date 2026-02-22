package com.api.erp.v1.main.features.customer.application.dto;

public record CustomerPreferenciasDto(
        String emailPrincipal,
        String emailNfe,
        Boolean enviarEmail,
        Boolean malaDireta
) {
}

