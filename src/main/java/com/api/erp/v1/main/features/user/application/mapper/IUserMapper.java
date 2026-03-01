package com.api.erp.v1.main.features.user.application.mapper;

import com.api.erp.v1.main.features.contact.application.dto.response.ContactResponse;
import com.api.erp.v1.main.features.contact.domain.entity.Contact;
import com.api.erp.v1.main.features.user.application.dto.response.UserResponse;
import com.api.erp.v1.main.features.user.domain.entity.User;
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

    default Set<ContactResponse> mapearContacts(Set<Contact> contacts) {
        if (contacts == null || contacts.isEmpty()) {
            return Set.of();
        }

        return contacts.stream()
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
}
