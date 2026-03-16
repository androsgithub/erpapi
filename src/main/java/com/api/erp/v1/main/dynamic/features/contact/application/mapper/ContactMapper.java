package com.api.erp.v1.main.dynamic.features.contact.application.mapper;

import com.api.erp.v1.main.dynamic.features.contact.application.dto.response.ContactResponse;
import com.api.erp.v1.main.dynamic.features.contact.domain.entity.Contact;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ContactMapper {

    public ContactResponse toResponse(Contact contact) {
        if (contact == null) {
            return null;
        }
        return new ContactResponse(
                contact.getId(),
                contact.getTipo() != null ? contact.getTipo().toString() : null,
                contact.getValor(),
                contact.getDescricao(),
                contact.isPrincipal(),
                contact.isAtivo(),
                contact.getCreatedAt().toLocalDateTime(),
                contact.getUpdatedAt().toLocalDateTime()
        );
    }

    public Set<ContactResponse> toResponse(Set<Contact> contacts) {
        if (contacts == null) {
            return Set.of();
        }
        return contacts.stream()
                .map(this::toResponse)
                .collect(Collectors.toSet());
    }
}
