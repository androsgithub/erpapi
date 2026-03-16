package com.api.erp.v1.main.dynamic.features.address.application.dto;

public record AddressDadosGeograficosDto(
        String rua,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String estado,
        String cep
) {
}
