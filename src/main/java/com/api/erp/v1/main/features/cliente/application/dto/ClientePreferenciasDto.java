package com.api.erp.v1.main.features.cliente.application.dto;

public record ClientePreferenciasDto(
        String emailPrincipal,
        String emailNfe,
        Boolean enviarEmail,
        Boolean malaDireta
) {
}

