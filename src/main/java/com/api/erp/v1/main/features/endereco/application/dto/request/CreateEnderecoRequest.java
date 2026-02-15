package com.api.erp.v1.main.features.endereco.application.dto.request;

import com.api.erp.v1.main.features.endereco.domain.entity.EnderecoTipo;

import java.util.Map;

public record CreateEnderecoRequest(
        String rua,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String estado,
        String cep,
        EnderecoTipo tipo,
        Boolean principal,
        Map<String, Object> customData
) {
}
