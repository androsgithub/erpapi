package com.api.erp.v1.features.endereco.application.dto.request;

import com.api.erp.v1.features.endereco.domain.entity.EnderecoTipo;
import com.api.erp.v1.shared.domain.valueobject.CustomData;

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
        CustomData customData
) {
}
