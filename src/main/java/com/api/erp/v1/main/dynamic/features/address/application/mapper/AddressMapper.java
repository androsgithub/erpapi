package com.api.erp.v1.main.dynamic.features.address.application.mapper;

import com.api.erp.v1.main.dynamic.features.address.application.dto.response.AddressResponse;
import com.api.erp.v1.main.dynamic.features.address.domain.entity.Address;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AddressMapper {

    public AddressResponse toResponse(Address address) {
        if (address == null) {
            return null;
        }
        return new AddressResponse(
                address.getId(),
                address.getStreet(),
                address.getNumber(),
                address.getComplement(),
                address.getDistrict(),
                address.getCity(),
                address.getState(),
                address.getPostalCode() != null ? address.getPostalCode().getValor() : null,
                address.getType(),
                address.getPrimary(),
                null, // customData
                address.getCreatedAt(),
                address.getUpdatedAt(),
                address.getCreatedBy(),
                address.getUpdatedBy()
        );
    }

    public List<AddressResponse> toResponseList(List<Address> addresses) {
        if (addresses == null) {
            return List.of();
        }
        return addresses.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
