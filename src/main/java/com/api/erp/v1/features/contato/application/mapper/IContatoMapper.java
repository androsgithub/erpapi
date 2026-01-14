package com.api.erp.v1.features.contato.application.mapper;

import com.api.erp.v1.features.contato.application.dto.response.ContatoResponse;
import com.api.erp.v1.features.contato.domain.entity.Contato;
import com.api.erp.v1.shared.application.mapper.BaseMapper;
import org.mapstruct.Mapper;

import java.util.Set;

@Mapper(componentModel = "spring", uses = BaseMapper.class)
public interface IContatoMapper {
    ContatoResponse toResponse(Contato contato);

    java.util.List<ContatoResponse> toResponseList(java.util.List<Contato> contatos);

    Set<ContatoResponse> toResponseSet(Set<Contato> contatos);
}
