package com.api.erp.v1.features.endereco.application.mapper;

import com.api.erp.v1.features.endereco.application.dto.response.EnderecoResponse;
import com.api.erp.v1.features.endereco.domain.entity.Endereco;
import com.api.erp.v1.shared.application.mapper.BaseMapper;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = BaseMapper.class)
public interface EnderecoMapper {

    EnderecoResponse toResponse(Endereco endereco);

    List<EnderecoResponse> toResponseList(List<Endereco> enderecos);
}
