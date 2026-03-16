package com.api.erp.v1.main.master.user.application.mapper;

import com.api.erp.v1.main.dynamic.features.contact.application.dto.response.ContactResponse;
import com.api.erp.v1.main.dynamic.features.contact.domain.entity.Contact;
import com.api.erp.v1.main.master.permission.application.dto.response.PermissionResponse;
import com.api.erp.v1.main.master.permission.application.dto.response.RoleResponse;
import com.api.erp.v1.main.master.permission.domain.entity.Permission;
import com.api.erp.v1.main.master.permission.domain.entity.Role;
import com.api.erp.v1.main.master.user.application.dto.response.UserPermissionsResponse;
import com.api.erp.v1.main.master.user.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface IUserPermissionsMapper {

    @Mapping(target = "email", expression = "java(user.getEmail() != null ? user.getEmail().getValor() : null)")
    @Mapping(target = "cpf", expression = "java(user.getCpf() != null ? user.getCpf().getFormatado() : null)")
    @Mapping(target = "contacts", expression = "java(mapearContacts(user.getContacts()))")
    @Mapping(target = "permissions", expression = "java(mapearPermissions(user.getPermissions()))")
    @Mapping(target = "roles", expression = "java(mapearRoles(user.getRoles()))")
    UserPermissionsResponse toResponse(User user);

    /**
     * Mapeia um conjunto de UserContact para um conjunto de ContactResponse
     * Extrai o Contact de dentro de cada UserContact e converte para DTO
     */
    default Set<ContactResponse> mapearContacts(Set<Contact> contacts) {
        if (contacts == null || contacts.isEmpty()) {
            return Set.of();
        }

        return contacts.stream()
                .filter(uc -> uc != null)
                .map(uc -> new ContactResponse(
                        uc.getId(),
                        uc.getTipo() != null ? uc.getTipo().toString() : null,
                        uc.getValor(),
                        uc.getDescricao(),
                        uc.isPrincipal(),
                        uc.isAtivo(),
//                        uc.getCustomData(),
                        uc.getCreatedAt().toLocalDateTime(),
                        uc.getUpdatedAt().toLocalDateTime()
                ))
                .collect(Collectors.toSet());
    }

    default Set<PermissionResponse> mapearPermissions(Set<Permission> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return Set.of();
        }

        return permissions.stream()
                .filter(uc -> uc != null)
                .map(uc -> new PermissionResponse(
                        uc.getId(),
                        uc.getCodigo(),
                        uc.getNome(),
                        uc.getModulo(),
                        uc.getAcao()
                ))
                .collect(Collectors.toSet());
    }

    default Set<RoleResponse> mapearRoles(Set<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return Set.of();
        }

        return roles.stream()
                .filter(uc -> uc != null)
                .map(uc -> new RoleResponse(
                        uc.getId(),
                        uc.getNome(),
                        // Extrai as permissões de cada RolePermission
                        uc.getPermissions() != null
                                ? uc.getPermissions()
                                .stream().filter(Objects::nonNull)
                                .collect(Collectors.toSet())
                                : Set.of()
                ))
                .collect(Collectors.toSet());
    }
}



