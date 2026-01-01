package com.api.erp.v1.features.cliente.application.dto;

public record ClienteDadosFinanceirosDto(
        Double limiteCredito,
        Double limiteDesconto,
        Boolean restricaoFinanceira,
        Boolean protestado
) {
}

