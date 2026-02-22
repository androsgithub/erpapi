package com.api.erp.v1.main.features.address.application.dto;

import com.api.erp.v1.main.features.address.domain.entity.AddressTipo;

public record AddressPropriedadesDto(
        AddressTipo tipo,
        Boolean principal
) {
}
