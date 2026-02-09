package com.api.erp.v1.features.usuario.application.mapper;

import com.api.erp.v1.features.contato.application.dto.response.ContatoResponse;
import com.api.erp.v1.features.permissao.application.dto.response.PermissaoResponse;
import com.api.erp.v1.features.permissao.application.dto.response.RoleResponse;
import com.api.erp.v1.features.usuario.application.dto.response.UsuarioPermissoesResponse;
import com.api.erp.v1.features.usuario.domain.entity.Usuario;
import com.api.erp.v1.features.usuario.domain.entity.UsuarioContato;
import com.api.erp.v1.features.usuario.domain.entity.UsuarioPermissao;
import com.api.erp.v1.features.usuario.domain.entity.UsuarioRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface IUsuarioPermissoesMapper {

    @Mapping(target = "email", expression = "java(usuario.getEmail() != null ? usuario.getEmail().getValor() : null)")
    @Mapping(target = "cpf", expression = "java(usuario.getCpf() != null ? usuario.getCpf().getFormatado() : null)")
    @Mapping(target = "contatos", expression = "java(mapearContatos(usuario.getContatos()))")
    @Mapping(target = "permissoes", expression = "java(mapearPermissoes(usuario.getPermissoes()))")
    @Mapping(target = "roles", expression = "java(mapearRoles(usuario.getRoles()))")
    UsuarioPermissoesResponse toResponse(Usuario usuario);

    /**
     * Mapeia um conjunto de UsuarioContato para um conjunto de ContatoResponse
     * Extrai o Contato de dentro de cada UsuarioContato e converte para DTO
     */
    default Set<ContatoResponse> mapearContatos(List<UsuarioContato> usuarioContatos) {
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
                        uc.getContato().getCreatedAt().toLocalDateTime(),
                        uc.getContato().getUpdatedAt().toLocalDateTime()
                ))
                .collect(Collectors.toSet());
    }

    default Set<PermissaoResponse> mapearPermissoes(List<UsuarioPermissao> usuarioPermissoes) {
        if (usuarioPermissoes == null || usuarioPermissoes.isEmpty()) {
            return Set.of();
        }

        return usuarioPermissoes.stream()
                .filter(uc -> uc.getPermissao() != null)
                .map(uc -> new PermissaoResponse(
                        uc.getPermissao().getId(),
                        uc.getPermissao().getCodigo(),
                        uc.getPermissao().getNome(),
                        uc.getPermissao().getModulo(),
                        uc.getPermissao().getAcao()
                ))
                .collect(Collectors.toSet());
    }

    default Set<RoleResponse> mapearRoles(List<UsuarioRole> usuarioRoles) {
        if (usuarioRoles == null || usuarioRoles.isEmpty()) {
            return Set.of();
        }

        return usuarioRoles.stream()
                .filter(uc -> uc.getRole() != null)
                .map(uc -> new RoleResponse(
                        uc.getRole().getId(),
                        uc.getRole().getNome(),
                        uc.getRole().getPermissoes()
                ))
                .collect(Collectors.toSet());
    }
}



