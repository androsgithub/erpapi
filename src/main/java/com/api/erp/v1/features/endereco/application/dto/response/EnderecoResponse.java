package com.api.erp.v1.features.endereco.application.dto.response;

import com.api.erp.v1.features.endereco.domain.entity.EnderecoTipo;
import com.api.erp.v1.shared.domain.valueobject.CEP;
import com.api.erp.v1.shared.domain.valueobject.CustomData;

import java.time.OffsetDateTime;

public record EnderecoResponse(
        Long id,
        String rua,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String estado,
        CEP cep,
        EnderecoTipo tipo,
        Boolean principal,
        CustomData custom_data,
        OffsetDateTime created_at,
        OffsetDateTime updated_at,
        Long created_by,
        Long updated_by
) {
}
