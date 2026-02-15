package com.api.erp.v1.main.features.endereco.application.dto;

public record EnderecoDadosGeograficosDto(
        String rua,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String estado,
        String cep
) {
}
