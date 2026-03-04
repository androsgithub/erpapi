package com.api.erp.v1.main.features.businesspartner.application.dto;


import com.dros.taxengine.domain.RegimeTributario;

public record BusinessPartnerDadosFiscaisDto(
        String razaoSocial,
        String nomeFantasia,
        String cnpj,
        String cpf,
        String rg,
        String inscricaoEstadual,
        String inscricaoMunicipal,
        RegimeTributario regimeTributario,
        Boolean icmsContribuinte,
        Double aliquotaIcms,
        String cnaePrincipal,
        Boolean consumidorFinal
) {
}
