package com.api.erp.v1.features.endereco.application.mapper;

import com.api.erp.v1.features.endereco.application.dto.EnderecoResponse;
import com.api.erp.v1.features.endereco.domain.entity.Endereco;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EnderecoMapper {

    EnderecoResponse toResponse(Endereco endereco);

    List<EnderecoResponse> toResponseList(List<Endereco> enderecos);
}
