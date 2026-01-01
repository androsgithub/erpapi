package com.api.erp.v1.features.usuario.application.mapper;

import com.api.erp.v1.features.contato.application.dto.response.ContatoResponse;
import com.api.erp.v1.features.contato.domain.entity.UsuarioContato;
import com.api.erp.v1.features.permissao.application.dto.response.PermissaoResponse;
import com.api.erp.v1.features.permissao.application.dto.response.RoleResponse;
import com.api.erp.v1.features.usuario.application.dto.response.UsuarioPermissoesResponse;
import com.api.erp.v1.features.usuario.domain.entity.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface IUsuarioPermissoesMapper {

    @Mapping(target = "email", expression = "java(usuario.getEmail() != null ? usuario.getEmail().getValor() : null)")
    @Mapping(target = "cpf", expression = "java(usuario.getCpf() != null ? usuario.getCpf().getFormatado() : null)")
    @Mapping(target = "contatos", expression = "java(mapearContatos(usuario.getContatos()))")
    @Mapping(target = "permissoes", expression = "java(mapearPermissoes(usuario.getPermissoes()))")
    @Mapping(target = "roles", expression = "java(mapearRoles(usuario.getPermissoes()))")
    UsuarioPermissoesResponse toResponse(Usuario usuario);

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

    /**
     * Mapeia permissões diretas de UsuarioPermissao para um conjunto de PermissaoResponse
     * Extrai todas as permissões diretas de todas as permissões do usuário
     */
    default Set<PermissaoResponse> mapearPermissoes(Set<com.api.erp.v1.features.permissao.domain.entity.UsuarioPermissao> usuarioPermissoes) {
        if (usuarioPermissoes == null || usuarioPermissoes.isEmpty()) {
            return Set.of();
        }
        
        return usuarioPermissoes.stream()
            .filter(up -> up.getPermissoesDiretas() != null && !up.getPermissoesDiretas().isEmpty())
            .flatMap(up -> up.getPermissoesDiretas().stream())
            .map(permissao -> new PermissaoResponse(
                permissao.getId(),
                permissao.getCodigo(),
                permissao.getNome(),
                permissao.getModulo(),
                permissao.getAcao(),
                permissao.getContexto(),
                permissao.isAtivo()
            ))
            .collect(Collectors.toSet());
    }

    /**
     * Mapeia roles de UsuarioPermissao para um conjunto de RoleResponse
     * Extrai todos os roles de todas as permissões do usuário
     */
    default Set<RoleResponse> mapearRoles(Set<com.api.erp.v1.features.permissao.domain.entity.UsuarioPermissao> permissoes) {
        if (permissoes == null || permissoes.isEmpty()) {
            return Set.of();
        }
        
        return permissoes.stream()
            .filter(up -> up.getRoles() != null && !up.getRoles().isEmpty())
            .flatMap(up -> up.getRoles().stream())
            .map(role -> new RoleResponse(
                role.getId(),
                role.getNome(),
                role.getPermissoes()
            ))
            .collect(Collectors.toSet());
    }
}



