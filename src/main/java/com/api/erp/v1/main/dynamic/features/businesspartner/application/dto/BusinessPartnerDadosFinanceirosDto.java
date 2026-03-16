package com.api.erp.v1.main.dynamic.features.businesspartner.application.dto;

public record BusinessPartnerDadosFinanceirosDto(
        Double limiteCredito,
        Double limiteDesconto,
        Boolean restricaoFinanceira,
        Boolean protestado
) {
}

