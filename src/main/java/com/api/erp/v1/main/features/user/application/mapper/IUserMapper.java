package com.api.erp.v1.main.features.user.application.mapper;

import com.api.erp.v1.main.features.contact.application.dto.response.ContactResponse;
import com.api.erp.v1.main.features.user.application.dto.response.UserResponse;
import com.api.erp.v1.main.features.user.domain.entity.User;
import com.api.erp.v1.main.features.user.domain.entity.UserContact;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface IUserMapper {

    @Mapping(target = "email", expression = "java(user.getEmail() != null ? user.getEmail().getValor() : null)")
    @Mapping(target = "cpf", expression = "java(user.getCpf() != null ? user.getCpf().getFormatado() : null)")
    @Mapping(target = "contacts", expression = "java(mapearContacts(user.getContacts()))")
    UserResponse toResponse(User user);

    List<UserResponse> toResponseList(List<User> users);

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
}
