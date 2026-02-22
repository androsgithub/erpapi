package com.api.erp.v1.main.features.address.application.dto.response;

import com.api.erp.v1.main.features.address.domain.entity.AddressTipo;
import com.api.erp.v1.main.shared.domain.valueobject.CEP;

import java.time.OffsetDateTime;
import java.util.Map;

public record AddressResponse(
        Long id,
        String rua,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String estado,
        CEP cep,
        AddressTipo tipo,
        Boolean principal,
        Map<String, Object> custom_data,
        OffsetDateTime created_at,
        OffsetDateTime updated_at,
        Long created_by,
        Long updated_by
) {
}
