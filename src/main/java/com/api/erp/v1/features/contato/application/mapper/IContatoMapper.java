package com.api.erp.v1.features.contato.application.mapper;

import com.api.erp.v1.features.contato.application.dto.response.ContatoResponse;
import com.api.erp.v1.features.contato.domain.entity.Contato;
import org.mapstruct.Mapper;

import java.util.Set;

/**
 * Mapper para conversão da entidade Contato para ContatoResponse
 * 
 * Utiliza MapStruct para gerar implementação em tempo de compilação
 * Centraliza toda a lógica de conversão domínio → DTO
 */
@Mapper(componentModel = "spring")
public interface IContatoMapper {
    ContatoResponse toResponse(Contato contato);
    java.util.List<ContatoResponse> toResponseList(java.util.List<Contato> contatos);
    Set<ContatoResponse> toResponseSet(Set<Contato> contatos);
}
