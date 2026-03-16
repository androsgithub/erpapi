package com.api.erp.v1.main.dynamic.features.address.application.mapper;

import com.api.erp.v1.main.dynamic.features.address.application.dto.response.AddressResponse;
import com.api.erp.v1.main.dynamic.features.address.domain.entity.Address;
import com.api.erp.v1.main.shared.application.mapper.BaseMapper;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = BaseMapper.class)
public interface AddressMapper {

    AddressResponse toResponse(Address address);

    List<AddressResponse> toResponseList(List<Address> addresses);
}
