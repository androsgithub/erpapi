package com.api.erp.v1.features.cliente.application.mapper;

import com.api.erp.v1.features.cliente.application.dto.response.ClienteSimpleResponseDto;
import com.api.erp.v1.features.cliente.domain.entity.Cliente;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface IClienteSimpleMapper {

    ClienteSimpleResponseDto toResponse(Cliente cliente);

    List<ClienteSimpleResponseDto> toResponseList(List<Cliente> clientes);

    default Page<ClienteSimpleResponseDto> toResponsePage(Page<Cliente> page) {
        return page.map(this::toResponse);
    }
}
