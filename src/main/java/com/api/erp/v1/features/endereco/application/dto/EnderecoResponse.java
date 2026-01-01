package com.api.erp.v1.features.endereco.application.dto;

import com.api.erp.v1.shared.domain.valueobject.CEP;
import com.api.erp.v1.shared.domain.valueobject.CustomData;

import java.time.LocalDateTime;

public record EnderecoResponse(
        Long id,
        String rua,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String estado,
        CEP cep,
        CustomData customData,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao
) {
}
