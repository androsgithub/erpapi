package com.api.erp.v1.features.usuario.application.mapper;

import com.api.erp.v1.features.contato.application.dto.response.ContatoResponse;
import com.api.erp.v1.features.contato.domain.entity.UsuarioContato;
import com.api.erp.v1.features.usuario.application.dto.response.UsuarioResponse;
import com.api.erp.v1.features.usuario.domain.entity.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface IUsuarioMapper {

    @Mapping(target = "email", expression = "java(usuario.getEmail() != null ? usuario.getEmail().getValor() : null)")
    @Mapping(target = "cpf", expression = "java(usuario.getCpf() != null ? usuario.getCpf().getFormatado() : null)")
    @Mapping(target = "contatos", expression = "java(mapearContatos(usuario.getContatos()))")
    UsuarioResponse toResponse(Usuario usuario);

    List<UsuarioResponse> toResponseList(List<Usuario> usuarios);

    /**
     * Mapeia um conjunto de UsuarioContato para um conjunto de ContatoResponse
     * Extrai o Contato de dentro de cada UsuarioContato e converte para DTO
     */
    default Set<ContatoResponse> mapearContatos(Set<UsuarioContato> usuarioContatos) {
        if (usuarioContatos == null || usuarioContatos.isEmpty()) {
            return Set.of();
        }
        
        return usuarioContatos.stream()
            .filter(uc -> uc.getContato() != null)
            .map(uc -> new ContatoResponse(
                uc.getContato().getId(),
                uc.getContato().getTipo() != null ? uc.getContato().getTipo().toString() : null,
                uc.getContato().getValor(),
                uc.getContato().getDescricao(),
                uc.getContato().isPrincipal(),
                uc.getContato().isAtivo(),
                uc.getContato().getCustomData(),
                uc.getContato().getDataCriacao(),
                uc.getContato().getDataAtualizacao()
            ))
            .collect(Collectors.toSet());
    }
}
