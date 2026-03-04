package com.api.erp.v1.main.features.address.application.dto;

import com.api.erp.v1.main.features.address.domain.entity.AddressType;

public record AddressPropriedadesDto(
        AddressType tipo,
        Boolean principal
) {
}
