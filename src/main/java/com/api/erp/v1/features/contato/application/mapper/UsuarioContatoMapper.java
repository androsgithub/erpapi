package com.api.erp.v1.features.contato.application.mapper;

import com.api.erp.v1.features.contato.application.dto.response.UsuarioContatosResponse;
import com.api.erp.v1.features.contato.domain.entity.UsuarioContato;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper para conversão da entidade UsuarioContato para UsuarioContatosResponse
 * 
 * Utiliza MapStruct com mapeamento customizado para campos complexos
 */
@Mapper(componentModel = "spring", uses = IContatoMapper.class)
public interface UsuarioContatoMapper {

    /**
     * Converte uma entidade UsuarioContato para UsuarioContatosResponse
     * 
     * Mapeamentos especiais:
     * - id → usuarioContatoId (nomes diferentes)
     * - usuario.id → usuarioId (extração de campo aninhado)
     * - contatos → contatos (conversão automática via ContatoMapper)
     * 
     * @param usuarioContato a entidade de domínio
     * @return o DTO de resposta
     */
    @Mapping(target = "usuarioContatoId", source = "id")
    @Mapping(target = "usuarioId", source = "usuario.id")
    UsuarioContatosResponse toResponse(UsuarioContato usuarioContato);

    /**
     * Converte uma coleção de UsuarioContatos para UsuarioContatosResponse
     * 
     * @param usuarioContatos lista de entidades
     * @return lista de DTOs de resposta
     */
    List<UsuarioContatosResponse> toResponseList(List<UsuarioContato> usuarioContatos);
}
