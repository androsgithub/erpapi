package com.api.erp.v1.features.cliente.application.dto;

public record ClientePreferenciasDto(
        String emailPrincipal,
        String emailNfe,
        Boolean enviarEmail,
        Boolean malaDireta
) {
}

