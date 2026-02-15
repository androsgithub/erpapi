package com.api.erp.v1.main.features.endereco.application.dto;

import com.api.erp.v1.main.features.endereco.domain.entity.EnderecoTipo;

public record EnderecoPropriedadesDto(
        EnderecoTipo tipo,
        Boolean principal
) {
}
