package com.api.erp.v1.main.features.businesspartner.application.dto;

public record BusinessPartnerDadosFinanceirosDto(
        Double limiteCredito,
        Double limiteDesconto,
        Boolean restricaoFinanceira,
        Boolean protestado
) {
}

