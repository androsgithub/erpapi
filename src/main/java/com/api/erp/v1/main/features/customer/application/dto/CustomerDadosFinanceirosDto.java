package com.api.erp.v1.main.features.customer.application.dto;

public record CustomerDadosFinanceirosDto(
        Double limiteCredito,
        Double limiteDesconto,
        Boolean restricaoFinanceira,
        Boolean protestado
) {
}

