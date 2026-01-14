package com.api.erp.v1.features.endereco.application.dto;

import com.api.erp.v1.features.endereco.domain.entity.EnderecoTipo;

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
