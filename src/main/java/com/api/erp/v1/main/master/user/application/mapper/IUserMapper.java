package com.api.erp.v1.main.master.user.application.mapper;

import com.api.erp.v1.main.dynamic.features.contact.application.dto.response.ContactResponse;
import com.api.erp.v1.main.dynamic.features.contact.domain.entity.Contact;
import com.api.erp.v1.main.master.user.application.dto.response.UserResponse;
import com.api.erp.v1.main.master.user.domain.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class IUserMapper {

    public UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }

        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setNomeCompleto(user.getName());
        response.setEmail(user.getEmail() != null ? user.getEmail().getValor() : null);
        response.setCpf(user.getCpf() != null ? user.getCpf().getFormatado() : null);
        response.setStatus(user.getStatus());
        response.setDataCriacao(user.getCreatedAt() != null ? user.getCreatedAt().toLocalDateTime() : null);
        response.setContacts(mapearContacts(user.getContacts()));
        response.setTenantId(user.getTenants() != null && !user.getTenants().isEmpty() 
                ? user.getTenants().stream().findFirst().orElseThrow().getId() 
                : 0);

        return response;
    }

    public List<UserResponse> toResponseList(List<User> users) {
        if (users == null) {
            return List.of();
        }
        return users.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public Set<ContactResponse> mapearContacts(Set<Contact> contacts) {
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
