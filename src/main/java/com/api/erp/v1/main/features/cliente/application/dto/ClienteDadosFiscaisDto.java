package com.api.erp.v1.main.features.cliente.application.dto;


import com.dros.taxengine.domain.RegimeTributario;

public record ClienteDadosFiscaisDto(
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
