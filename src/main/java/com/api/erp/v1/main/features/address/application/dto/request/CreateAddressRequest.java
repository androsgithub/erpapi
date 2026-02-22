package com.api.erp.v1.main.features.address.application.dto.request;

import com.api.erp.v1.main.features.address.domain.entity.AddressTipo;

import java.util.Map;

public record CreateAddressRequest(
        String rua,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String estado,
        String cep,
        AddressTipo tipo,
        Boolean principal,
        Map<String, Object> customData
) {
}
