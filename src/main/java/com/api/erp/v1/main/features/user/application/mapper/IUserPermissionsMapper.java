package com.api.erp.v1.main.features.user.application.mapper;

import com.api.erp.v1.main.features.contact.application.dto.response.ContactResponse;
import com.api.erp.v1.main.features.permission.application.dto.response.PermissionResponse;
import com.api.erp.v1.main.features.permission.application.dto.response.RoleResponse;
import com.api.erp.v1.main.features.permission.domain.entity.RolePermission;
import com.api.erp.v1.main.features.user.application.dto.response.UserPermissionsResponse;
import com.api.erp.v1.main.features.user.domain.entity.User;
import com.api.erp.v1.main.features.user.domain.entity.UserContact;
import com.api.erp.v1.main.features.user.domain.entity.UserPermission;
import com.api.erp.v1.main.features.user.domain.entity.UserRole;
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
    default Set<ContactResponse> mapearContacts(Set<UserContact> userContacts) {
        if (userContacts == null || userContacts.isEmpty()) {
            return Set.of();
        }

        return userContacts.stream()
                .filter(uc -> uc.getContact() != null)
                .map(uc -> new ContactResponse(
                        uc.getContact().getId(),
                        uc.getContact().getTipo() != null ? uc.getContact().getTipo().toString() : null,
                        uc.getContact().getValor(),
                        uc.getContact().getDescricao(),
                        uc.getContact().isPrincipal(),
                        uc.getContact().isAtivo(),
//                        uc.getContact().getCustomData(),
                        uc.getContact().getCreatedAt().toLocalDateTime(),
                        uc.getContact().getUpdatedAt().toLocalDateTime()
                ))
                .collect(Collectors.toSet());
    }

    default Set<PermissionResponse> mapearPermissions(Set<UserPermission> userPermissions) {
        if (userPermissions == null || userPermissions.isEmpty()) {
            return Set.of();
        }

        return userPermissions.stream()
                .filter(uc -> uc.getPermission() != null)
                .map(uc -> new PermissionResponse(
                        uc.getPermission().getId(),
                        uc.getPermission().getCodigo(),
                        uc.getPermission().getNome(),
                        uc.getPermission().getModulo(),
                        uc.getPermission().getAcao()
                ))
                .collect(Collectors.toSet());
    }

    default Set<RoleResponse> mapearRoles(Set<UserRole> userRoles) {
        if (userRoles == null || userRoles.isEmpty()) {
            return Set.of();
        }

        return userRoles.stream()
                .filter(uc -> uc.getRole() != null)
                .map(uc -> new RoleResponse(
                        uc.getRole().getId(),
                        uc.getRole().getNome(),
                        // Extrai as permissões de cada RolePermission
                        uc.getRole().getPermissions() != null
                                ? uc.getRole().getPermissions().stream()
                                    .map(RolePermission::getPermission)
                                    .filter(Objects::nonNull)
                                    .collect(Collectors.toSet())
                                : Set.of()
                ))
                .collect(Collectors.toSet());
    }
}



